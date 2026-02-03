package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByIdentification(String identification);
}
