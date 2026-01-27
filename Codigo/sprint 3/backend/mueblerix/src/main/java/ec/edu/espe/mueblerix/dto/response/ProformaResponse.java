package ec.edu.espe.mueblerix.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProformaResponse {

    private Long id;
    private String code;
    private CustomerInfo customer;
    private List<ProformaDetailInfo> details;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal tax;
    private BigDecimal total;
    private LocalDateTime emissionDate;
    private String createdByUser;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerInfo {
        private Long id;
        private String name;
        private String identification;
        private String address;
        private String phone;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProformaDetailInfo {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal unitDiscount;
        private BigDecimal subtotal;
        private OfferInfo appliedOffer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OfferInfo {
        private Long id;
        private String type;
        private BigDecimal discountValue;
        private BigDecimal promotionalPrice;
    }
}
