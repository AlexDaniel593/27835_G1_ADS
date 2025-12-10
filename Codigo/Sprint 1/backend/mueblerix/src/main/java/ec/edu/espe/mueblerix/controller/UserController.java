package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.ChangePasswordRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.security.UserDetailsImpl;
import ec.edu.espe.mueblerix.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;

  @PutMapping("/change-password")
  public ResponseEntity<ApiResponse<Void>> changePassword(
          @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
          @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    log.info("Password change request for user: {}", userDetails.getIdentification());
    
    try {
      userService.changePassword(userDetails.getIdentification(), changePasswordRequest);
      return ResponseEntity.ok(ApiResponse.success("Contraseña cambiada exitosamente", null));
    } catch (IllegalArgumentException e) {
      log.warn("Password change failed for user {}: {}", userDetails.getIdentification(), e.getMessage());
      return ResponseEntity.badRequest().body(ApiResponse.error("Error al cambiar la contraseña", e.getMessage()));
    }
  }
}
