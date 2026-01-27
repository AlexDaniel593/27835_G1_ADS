package ec.edu.espe.mueblerix.service;

import ec.edu.espe.mueblerix.model.User;
import ec.edu.espe.mueblerix.repository.UserRepository;
import ec.edu.espe.mueblerix.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByIdentification(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    return UserDetailsImpl.build(user);
  }

}
