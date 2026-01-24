package ec.edu.espe.mueblerix.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 10)
  private String identification;

  @Column(nullable = false, length = 150)
  private String name;

  @Column(length = 300)
  private String address;

  @Column(nullable = false, length = 15)
  private String phone;

  @Column(length = 100)
  private String email;

  @OneToMany(
          mappedBy = "customer",
          fetch = FetchType.LAZY,
          cascade = CascadeType.ALL,
          orphanRemoval = true
  )
  @Builder.Default
  private Set<Proforma> proformas = new HashSet<>();

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
