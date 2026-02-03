package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.LoginRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.AuthResponse;
import ec.edu.espe.mueblerix.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoints para autenticación de usuarios")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  @Operation(
      summary = "Iniciar sesión",
      description = "Autenticación de usuario mediante credenciales (username y password). Retorna un token JWT para acceso a endpoints protegidos."
  )
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "Autenticación exitosa",
          content = @Content(schema = @Schema(implementation = AuthResponse.class))
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "401",
          description = "Credenciales inválidas",
          content = @Content
      ),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "400",
          description = "Datos de entrada inválidos",
          content = @Content
      )
  })
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
    log.info("Login attempt for user: {}", loginRequest.getUsername());
    AuthResponse authResponse = authService.authenticate(loginRequest);
    return ResponseEntity.ok(ApiResponse.success("Autenticación exitosa", authResponse));
  }
}
