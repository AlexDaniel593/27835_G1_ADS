package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByIsActiveTrue();

    Optional<Material> findByName(String name);

    Optional<Material> findByIdAndIsActiveTrue(Long id);

    boolean existsByName(String name);

    List<Material> findByIdInAndIsActiveTrue(List<Long> ids);
}
