package ec.edu.espe.mueblerix.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProformaRequest {

    @NotNull(message = "Los datos del cliente son obligatorios")
    private CustomerData customer;

    @NotNull(message = "Debe incluir al menos un producto")
    @Size(min = 1, message = "Debe seleccionar al menos un producto")
    private List<ProformaDetailRequest> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerData {
        
        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        private String name;

        @NotBlank(message = "La cédula es obligatoria")
        @Pattern(regexp = "^\\d{10}$", message = "La cédula debe tener 10 dígitos")
        private String identification;

        @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
        private String address;

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener 10 dígitos")
        private String phone;

        @Email(message = "El email debe ser válido")
        @Size(max = 100, message = "El email no puede exceder 100 caracteres")
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProformaDetailRequest {
        
        @NotNull(message = "El ID del producto es obligatorio")
        @Positive(message = "El ID del producto debe ser positivo")
        private Long productId;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer quantity;
    }
}
