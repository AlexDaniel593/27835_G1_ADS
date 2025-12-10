package ec.edu.espe.mueblerix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

  @NotBlank(message = "La contraseña actual no puede estar vacía")
  private String currentPassword;

  @NotBlank(message = "La nueva contraseña no puede estar vacía")
  @Size(min = 8, max = 20, message = "La contraseña debe tener entre 8 y 20 caracteres")
  @Pattern(
          regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
          message = "La contraseña debe contener al menos un dígito, una minúscula, una mayúscula y un carácter especial (@#$%^&+=!)"
  )
  private String newPassword;

  @NotBlank(message = "La confirmación de contraseña no puede estar vacía")
  private String confirmPassword;
}
