package ec.edu.espe.mueblerix.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"products"})
@ToString(exclude = {"products"})
public class Material {

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

  @ManyToMany(mappedBy = "materials", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Product> products = new HashSet<>();

}
