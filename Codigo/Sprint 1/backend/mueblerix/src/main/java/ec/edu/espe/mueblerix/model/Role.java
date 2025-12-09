package ec.edu.espe.mueblerix.model;

import ec.edu.espe.mueblerix.model.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  @Enumerated(EnumType.STRING)
  private RoleName name;

  @Column(length = 255)
  private String description;

  @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<User> users = new HashSet<>();

}