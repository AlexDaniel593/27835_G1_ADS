package ec.edu.espe.mueblerix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateColorRequest {

    @NotBlank(message = "El nombre del color es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String name;
}
