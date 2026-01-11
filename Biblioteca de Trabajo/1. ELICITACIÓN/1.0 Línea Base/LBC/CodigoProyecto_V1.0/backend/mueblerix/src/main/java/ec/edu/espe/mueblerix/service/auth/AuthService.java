package ec.edu.espe.mueblerix.service.auth;

import ec.edu.espe.mueblerix.dto.request.LoginRequest;
import ec.edu.espe.mueblerix.dto.response.AuthResponse;

public interface AuthService {

   AuthResponse authenticate(LoginRequest loginRequest);
}
