package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.ChangePasswordRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.security.UserDetailsImpl;
import ec.edu.espe.mueblerix.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Usuarios", description = "Gestión de usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final UserService userService;

  @PutMapping("/change-password")
  @Operation(summary = "Cambiar contraseña", description = "Permite al usuario autenticado cambiar su contraseña actual")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o contraseña actual incorrecta", content = @Content),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
  })
  public ResponseEntity<ApiResponse<Void>> changePassword(
          @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para cambio de contraseña", required = true)
          @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
          @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    try {
      userService.changePassword(userDetails.getIdentification(), changePasswordRequest);
      return ResponseEntity.ok(ApiResponse.success("Contraseña cambiada exitosamente", null));
    } catch (IllegalArgumentException e) {
      log.warn("Password change validation error: {}", e.getMessage());
      return ResponseEntity.badRequest().body(ApiResponse.error("Error al cambiar la contraseña", e.getMessage()));
    } catch (Exception e) {
      log.error("Unexpected error changing password", e);
      return ResponseEntity.internalServerError().body(ApiResponse.error("Error interno del servidor", e.getMessage()));
    }
  }
}

