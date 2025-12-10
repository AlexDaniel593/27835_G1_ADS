package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByIsActiveTrue();

    Optional<Category> findByName(String name);

    Optional<Category> findByIdAndIsActiveTrue(Long id);

    boolean existsByName(String name);
}
