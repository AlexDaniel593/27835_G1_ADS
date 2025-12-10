package ec.edu.espe.mueblerix.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio no es válido")
    private BigDecimal price;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    private List<Long> materialIds;

    private List<Long> colorIds;

    private List<String> imageUrls;
}
