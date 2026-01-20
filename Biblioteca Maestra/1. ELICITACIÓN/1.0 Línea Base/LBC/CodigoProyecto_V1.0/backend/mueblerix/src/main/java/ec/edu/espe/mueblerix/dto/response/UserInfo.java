package ec.edu.espe.mueblerix.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
  private Long id;
  private String identification;
  private String email;
  private String firstName;
  private String lastName;
  private List<String> roles;
}