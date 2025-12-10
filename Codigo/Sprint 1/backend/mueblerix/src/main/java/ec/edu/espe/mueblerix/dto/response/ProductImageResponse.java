package ec.edu.espe.mueblerix.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponse {

    private Long id;
    private String url;
    private Integer order;
    private Boolean isPrimary;
    private LocalDateTime uploadedAt;
}
