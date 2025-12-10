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
public class ProductResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private CategoryResponse category;
    private List<MaterialResponse> materials;
    private List<ColorResponse> colors;
    private List<ProductImageResponse> images;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
