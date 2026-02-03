package ec.edu.espe.mueblerix.model;

import ec.edu.espe.mueblerix.model.enums.OfferType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  private OfferType type;

  @Column(precision = 10, scale = 2)
  private BigDecimal discountValue;

  @Column(precision = 10, scale = 2)
  private BigDecimal promotionalPrice;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creation_user_id")
  private User creationUser;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}