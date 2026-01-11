package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.LoginRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.AuthResponse;
import ec.edu.espe.mueblerix.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
    log.info("Login attempt for user: {}", loginRequest.getUsername());
    AuthResponse authResponse = authService.authenticate(loginRequest);
    return ResponseEntity.ok(ApiResponse.success("Autenticación exitosa", authResponse));
  }
}
