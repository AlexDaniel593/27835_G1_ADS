package ec.edu.espe.mueblerix.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "proforma_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProformaDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "proforma_id", nullable = false)
  private Proforma proforma;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal unitPrice;

  @Column(precision = 10, scale = 2)
  @Builder.Default
  private BigDecimal unitDiscount = BigDecimal.ZERO;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal subtotal;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "applied_offer_id")
  private Offer appliedOffer;
}
