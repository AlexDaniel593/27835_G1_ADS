package ec.edu.espe.mueblerix.service.user;


import ec.edu.espe.mueblerix.dto.request.ChangePasswordRequest;
import ec.edu.espe.mueblerix.model.User;

public interface UserService {

   void recordFailedLoginAttempt(String username);
   User findByIdentification(String identification);
   void changePassword(String identification, ChangePasswordRequest changePasswordRequest);

}
