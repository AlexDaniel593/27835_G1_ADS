package ec.edu.espe.mueblerix.service.auth;

import ec.edu.espe.mueblerix.dto.mapperFactory.UserMapper;
import ec.edu.espe.mueblerix.dto.request.LoginRequest;
import ec.edu.espe.mueblerix.dto.response.AuthResponse;
import ec.edu.espe.mueblerix.model.User;
import ec.edu.espe.mueblerix.security.UserDetailsImpl;
import ec.edu.espe.mueblerix.security.jwt.JwtService;
import ec.edu.espe.mueblerix.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserService userService;

  @Transactional
  public AuthResponse authenticate(LoginRequest loginRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      loginRequest.getUsername(),
                      loginRequest.getPassword()
              )
      );

      UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

      if (!user.isAccountNonLocked()) {
        throw new LockedException("The user is blocked");
      }

      if (!user.isEnabled()) {
        throw new DisabledException("The user is not enabled");
      }

      User userEntity = userService.findByIdentification(user.getIdentification());

      String accessToken = jwtService.generateAccessToken(user);

      return AuthResponse.builder()
              .accessToken(accessToken)
              .tokenType("Bearer")
              .expiresIn(jwtService.getAccessTokenExpiration())
              .userInfo(UserMapper.mapToUserInfo(userEntity))
              .build();
    } catch (BadCredentialsException e) {
      userService.recordFailedLoginAttempt(loginRequest.getUsername());
      log.warn("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
      throw new BadCredentialsException("Invalid username or password");
    } catch (LockedException e) {
      log.warn("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
      throw new LockedException("User account is locked");
    } catch (DisabledException e) {
      log.warn("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
      throw new DisabledException("User account is disabled");
    } catch (AuthenticationException e) {
      log.warn("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
      throw new AuthenticationServiceException("Authentication failed" + e.getMessage());
    }
  }
}
