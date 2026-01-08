package ec.edu.espe.mueblerix.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "colors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"products"})
@ToString(exclude = {"products"})
public class Color {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @ManyToMany(mappedBy = "colors", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<Product> products = new HashSet<>();


}