package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsActiveTrue();

    List<Product> findByIsActiveTrueAndIsDeletedFalse();

    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true AND p.isDeleted = false")
    List<Product> findActiveByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.isActive = true AND p.isDeleted = false")
    List<Product> searchByName(String name);
}
