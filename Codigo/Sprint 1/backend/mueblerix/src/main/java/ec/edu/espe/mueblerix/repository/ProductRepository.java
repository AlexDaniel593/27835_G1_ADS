package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.materials m " +
            "LEFT JOIN p.colors c " +
            "WHERE p.isActive = true AND p.isDeleted = false " +
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:materialId IS NULL OR m.id = :materialId) " +
            "AND (:colorId IS NULL OR c.id = :colorId) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> searchProductsAdvanced(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("materialId") Long materialId,
            @Param("colorId") Long colorId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}
