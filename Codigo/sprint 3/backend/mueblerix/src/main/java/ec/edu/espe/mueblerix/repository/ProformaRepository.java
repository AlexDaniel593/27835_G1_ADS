package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.Proforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProformaRepository extends JpaRepository<Proforma, Long> {
    
    Optional<Proforma> findByCode(String code);
    
    @Query("SELECT p FROM Proforma p " +
           "LEFT JOIN FETCH p.customer " +
           "LEFT JOIN FETCH p.details d " +
           "LEFT JOIN FETCH d.product " +
           "LEFT JOIN FETCH d.appliedOffer " +
           "LEFT JOIN FETCH p.creationUser " +
           "WHERE p.isDeleted = false ORDER BY p.emissionDate DESC")
    List<Proforma> findAllActive();
    
    @Query("SELECT p FROM Proforma p WHERE p.customer.id = :customerId AND p.isDeleted = false ORDER BY p.emissionDate DESC")
    List<Proforma> findByCustomerId(Long customerId);
    
    @Query("SELECT COUNT(p) FROM Proforma p WHERE YEAR(p.emissionDate) = YEAR(CURRENT_DATE) AND MONTH(p.emissionDate) = MONTH(CURRENT_DATE)")
    Long countProformasThisMonth();
}
