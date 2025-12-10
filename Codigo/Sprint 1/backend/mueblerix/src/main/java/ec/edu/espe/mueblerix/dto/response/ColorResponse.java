package ec.edu.espe.mueblerix.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorResponse {

    private Long id;
    private String name;
    private Boolean isActive;
}
