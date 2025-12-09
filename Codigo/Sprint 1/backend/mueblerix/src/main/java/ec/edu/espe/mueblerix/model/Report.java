package ec.edu.espe.mueblerix.model;

import ec.edu.espe.mueblerix.model.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  @Enumerated(EnumType.STRING)
  private ReportType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column
  private LocalDate startDate;

  @Column
  private LocalDate endDate;

  @Column
  private Integer month;

  @Column
  private Integer year;

  @Column(length = 500)
  private String filePath;

  @Column(nullable = false)
  private LocalDateTime generatedAt;

  @PrePersist
  protected void onCreate() {
    generatedAt = LocalDateTime.now();
  }
}