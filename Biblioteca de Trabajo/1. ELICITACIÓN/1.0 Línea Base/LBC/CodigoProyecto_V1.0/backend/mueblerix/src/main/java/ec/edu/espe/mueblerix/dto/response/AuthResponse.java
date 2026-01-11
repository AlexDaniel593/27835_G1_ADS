package ec.edu.espe.mueblerix.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
  private String accessToken;
  private String tokenType = "Bearer";
  private Long expiresIn;
  private UserInfo userInfo;
}
