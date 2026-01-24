package ec.edu.espe.mueblerix.dto.request;

import ec.edu.espe.mueblerix.model.enums.OfferType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOfferRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "El tipo de oferta es obligatorio")
    private OfferType type;

    @DecimalMin(value = "0.0", inclusive = false, message = "El valor del descuento debe ser mayor a 0")
    @DecimalMax(value = "100.0", message = "El descuento no puede ser mayor al 100%")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio promocional debe ser mayor a 0")
    private BigDecimal promotionalPrice;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;

    @NotNull(message = "El estado es obligatorio")
    private Boolean isActive;
}
