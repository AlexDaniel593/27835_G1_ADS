package ec.edu.espe.mueblerix.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
  @NotBlank(message = "El nombre de usuario no puede estar vacío")
  private String username;
  @NotBlank(message = "La contraseña no puede estar vacía")
  private String password;

}
