package ec.edu.espe.mueblerix.dto.response;

import ec.edu.espe.mueblerix.model.enums.OfferType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferResponse {

    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal originalPrice;
    private OfferType type;
    private BigDecimal discountValue;
    private BigDecimal promotionalPrice;
    private BigDecimal finalPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String status; // "VIGENTE", "PRÓXIMA", "EXPIRADA"
    private LocalDateTime createdAt;
    private String createdBy;
}
