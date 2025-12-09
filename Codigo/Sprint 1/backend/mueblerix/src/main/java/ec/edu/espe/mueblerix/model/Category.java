package ec.edu.espe.mueblerix.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @Column(length = 500)
  private String description;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @OneToMany(
          mappedBy = "category",
          fetch = FetchType.LAZY,
          cascade = CascadeType.ALL,
          orphanRemoval = true
  )
  @Builder.Default
  private Set<Product> products = new HashSet<>();

}
