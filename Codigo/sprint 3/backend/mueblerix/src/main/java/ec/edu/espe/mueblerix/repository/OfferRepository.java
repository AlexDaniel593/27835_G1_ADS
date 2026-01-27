package ec.edu.espe.mueblerix.repository;

import ec.edu.espe.mueblerix.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    // Obtener todas las ofertas activas
    List<Offer> findByIsActiveTrue();

    // Obtener todas las ofertas (activas y próximas)
    @Query("SELECT o FROM Offer o WHERE o.isActive = true ORDER BY o.startDate DESC")
    List<Offer> findAllActiveOffers();

    // Obtener ofertas por producto
    @Query("SELECT o FROM Offer o WHERE o.product.id = :productId AND o.isActive = true")
    List<Offer> findByProductId(@Param("productId") Long productId);

    // Validar si un producto ya tiene una oferta activa en un periodo específico (RF-08 A5)
    @Query("SELECT COUNT(o) > 0 FROM Offer o " +
           "WHERE o.product.id = :productId " +
           "AND o.isActive = true " +
           "AND (:startDate BETWEEN o.startDate AND o.endDate " +
           "     OR :endDate BETWEEN o.startDate AND o.endDate " +
           "     OR (o.startDate BETWEEN :startDate AND :endDate))")
    boolean existsActiveOfferForProductInPeriod(
            @Param("productId") Long productId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Validar si un producto tiene oferta activa excluyendo una oferta específica (para edición)
    @Query("SELECT COUNT(o) > 0 FROM Offer o " +
           "WHERE o.product.id = :productId " +
           "AND o.id != :offerId " +
           "AND o.isActive = true " +
           "AND (:startDate BETWEEN o.startDate AND o.endDate " +
           "     OR :endDate BETWEEN o.startDate AND o.endDate " +
           "     OR (o.startDate BETWEEN :startDate AND :endDate))")
    boolean existsActiveOfferForProductInPeriodExcludingOffer(
            @Param("productId") Long productId,
            @Param("offerId") Long offerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Obtener oferta por ID y que esté activa
    Optional<Offer> findByIdAndIsActiveTrue(Long id);

    // Obtener ofertas vigentes actualmente
    @Query("SELECT o FROM Offer o WHERE o.isActive = true " +
           "AND CURRENT_DATE BETWEEN o.startDate AND o.endDate " +
           "ORDER BY o.startDate DESC")
    List<Offer> findCurrentActiveOffers();

    // Obtener ofertas próximas
    @Query("SELECT o FROM Offer o WHERE o.isActive = true " +
           "AND CURRENT_DATE < o.startDate " +
           "ORDER BY o.startDate ASC")
    List<Offer> findUpcomingOffers();

    // Obtener ofertas expiradas
    @Query("SELECT o FROM Offer o WHERE o.isActive = true " +
           "AND CURRENT_DATE > o.endDate")
    List<Offer> findExpiredOffers();
}
