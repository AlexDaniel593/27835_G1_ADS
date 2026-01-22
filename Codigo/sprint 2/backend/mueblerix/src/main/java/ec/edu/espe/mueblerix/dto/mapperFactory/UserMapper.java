package ec.edu.espe.mueblerix.dto.mapperFactory;

import ec.edu.espe.mueblerix.dto.response.UserInfo;
import ec.edu.espe.mueblerix.model.User;

public class UserMapper {

  public static UserInfo mapToUserInfo(User user) {
    return UserInfo.builder()
            .id(user.getId())
            .identification(user.getIdentification())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .roles(
                    user.getRoles()
                            .stream()
                            .map(role -> role.getName().name())
                            .toList()
            )
            .build();
  }
}
