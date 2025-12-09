package ec.edu.espe.mueblerix.security;

import ec.edu.espe.mueblerix.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

  private Long id;
  private String identification;
  private String password;
  private String name;
  private String lastName;
  private String email;
  private boolean isActive;
  private boolean isBlocked;
  private Collection<? extends GrantedAuthority> grantedAuthorities; // validación de roles en los controllers

  public static UserDetailsImpl build(User user) {

    List<GrantedAuthority> authorities =
            user.getRoles().stream()
                    .map(role -> {
                      String roleName = role.getName().name();
                      if (!roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName;
                      }

                      return new SimpleGrantedAuthority(roleName);
                    })
                    .collect(Collectors.toList());

    return new UserDetailsImpl(
            user.getId(),
            user.getIdentification(),
            user.getPassword(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getIsActive(),
            user.getIsBlocked(),
            authorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return grantedAuthorities;
  }

  @Override
  public String getUsername() {
    return identification;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !isBlocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isActive;
  }
}
