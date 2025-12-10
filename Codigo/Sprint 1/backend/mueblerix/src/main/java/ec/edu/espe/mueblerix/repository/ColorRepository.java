package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

    List<Color> findByIsActiveTrue();

    Optional<Color> findByName(String name);

    Optional<Color> findByIdAndIsActiveTrue(Long id);

    boolean existsByName(String name);

    List<Color> findByIdInAndIsActiveTrue(List<Long> ids);
}
