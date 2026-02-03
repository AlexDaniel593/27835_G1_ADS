package ec.edu.espe.mueblerix.security.jwt;

import ec.edu.espe.mueblerix.security.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Getter
  @Value("${jwt.access-token.expiration}")
  private long accessTokenExpiration;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  public String buildAccessToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          Long expiration
  ) {
    UserDetailsImpl user = (UserDetailsImpl) userDetails;

    Map<String, Object> claims = new HashMap<>(extraClaims);

    claims.put("identification", user.getIdentification());
    claims.put("email", user.getEmail());

    claims.put("authorities",
            user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()
    );

    return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getIdentification())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    final Claims claims = extractAllClaims(token);
    return resolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parser()
              .setSigningKey(getSigningKey())
              .build()
              .parseClaimsJws(token)
              .getBody();

    } catch (JwtException e) {
      log.warn("Token error: {}", e.getMessage());
      throw e;
    }
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    try {
      final String idFromToken = extractIdentificationSub(token);
      UserDetailsImpl user = (UserDetailsImpl) userDetails;

      return idFromToken.equals(user.getIdentification()) &&
              !isTokenExpired(token);

    } catch (Exception e) {
      log.debug("Token validation failed: {}", e.getMessage());
      return false;
    }
  }

  public String extractIdentificationSub(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateAccessToken(UserDetails userDetails) {
    Map<String, Object> extra = new HashMap<>();
    extra.put("type", "access");

    return buildAccessToken(extra, userDetails, accessTokenExpiration);
  }
}
