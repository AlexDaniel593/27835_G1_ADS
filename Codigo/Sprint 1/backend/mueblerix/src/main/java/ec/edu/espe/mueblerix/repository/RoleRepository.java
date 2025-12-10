package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.Role;
import ec.edu.espe.mueblerix.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(RoleName name);
}
