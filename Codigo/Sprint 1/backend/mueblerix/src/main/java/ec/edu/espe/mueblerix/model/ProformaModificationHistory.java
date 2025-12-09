package ec.edu.espe.mueblerix.model;

import ec.edu.espe.mueblerix.model.enums.ModificationType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proforma_modification_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProformaModificationHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "proforma_id", nullable = false)
  private Proforma proforma;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  private ModificationType type;

  @Column(length = 1000)
  private String description;

  @Column(nullable = false)
  private LocalDateTime modificationDate;

  @PrePersist
  protected void onCreate() {
    modificationDate = LocalDateTime.now();
  }
}