package ec.edu.espe.mueblerix.service.user;

import ec.edu.espe.mueblerix.dto.request.ChangePasswordRequest;
import ec.edu.espe.mueblerix.model.User;
import ec.edu.espe.mueblerix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void recordFailedLoginAttempt(String identification) {
    User user = userRepository.findByIdentification(identification)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + identification));
    int attempts = user.getFailedLoginAttempts() + 1;
    log.debug("user:{}", attempts);
    user.setFailedLoginAttempts(attempts);

    if (attempts >= 5) {
      lockUserAccount(identification, 15); // Lock account for 15 minutes after 5 failed attempts
    }

    userRepository.save(user);
    log.info("Recorded failed login attempt for user: {}. Total attempts: {}", user.getIdentification(), attempts);
  }

  @Override
  @Transactional
  public User findByIdentification(String identification) {
    return userRepository.findByIdentification(identification)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with identification: " + identification));
  }

  @Transactional
  public void lockUserAccount(String identification, int durationMinutes) {
    User user = userRepository.findByIdentification(identification)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with identification: " + identification));
    user.setIsBlocked(true);
    userRepository.save(user);

    log.warn("User account locked: {}", user.getIdentification());
  }

  @Transactional
  @Override
  public void changePassword(String identification, ChangePasswordRequest changePasswordRequest) {
    User user = userRepository.findByIdentification(identification)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with identification: " + identification));

    // Verificar que las contraseñas coincidan
    if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
      throw new IllegalArgumentException("Las contraseñas no coinciden");
    }

    // Verificar que la contraseña actual sea correcta
    if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
      throw new IllegalArgumentException("La contraseña actual es incorrecta");
    }

    // Verificar que la nueva contraseña sea diferente a la actual
    if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
      throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");
    }

    // Actualizar la contraseña
    user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    
    // Si es el primer login, actualizamos el flag
    if (user.getFirstLogin() != null && user.getFirstLogin()) {
      user.setFirstLogin(false);
    }

    // Reiniciar intentos fallidos
    user.setFailedLoginAttempts(0);
    
    userRepository.save(user);
    log.info("Password changed for user: {}", identification);
  }

}
