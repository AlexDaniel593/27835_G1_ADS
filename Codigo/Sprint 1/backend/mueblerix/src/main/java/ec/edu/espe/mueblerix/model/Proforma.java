package ec.edu.espe.mueblerix.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "proformas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proforma {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String code; // Unique auto-generated ID

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @OneToMany(mappedBy = "proforma", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<ProformaDetail> details = new HashSet<>();

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal subtotal;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal totalDiscount;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal tax;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal total;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isDeleted = false;

  @Column
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deletion_user_id")
  private User deletionUser;

  @Column(nullable = false, updatable = false)
  private LocalDateTime emissionDate;

  @Column
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creation_user_id", nullable = false)
  private User creationUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "update_user_id")
  private User updateUser;

  @OneToMany(mappedBy = "proforma", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<ProformaModificationHistory> modificationHistory = new HashSet<>();

  @PrePersist
  protected void onCreate() {
    emissionDate = LocalDateTime.now();
    generateCode();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  private void generateCode() {
    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
    this.code = "PRF-" + LocalDateTime.now().format(formatter) + "-" + System.currentTimeMillis();
  }

}
