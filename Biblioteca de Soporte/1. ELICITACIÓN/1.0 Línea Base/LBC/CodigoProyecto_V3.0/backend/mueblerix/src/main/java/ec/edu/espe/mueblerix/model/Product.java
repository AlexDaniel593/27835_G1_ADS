package ec.edu.espe.mueblerix.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"materials", "colors", "images", "offers", "category"})
@ToString(exclude = {"materials", "colors", "images", "offers", "category"})
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "product_materials",
          joinColumns = @JoinColumn(name = "product_id"),
          inverseJoinColumns = @JoinColumn(name = "material_id")
  )
  @Builder.Default
  private Set<Material> materials = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "product_colors",
          joinColumns = @JoinColumn(name = "product_id"),
          inverseJoinColumns = @JoinColumn(name = "color_id")
  )
  @Builder.Default
  private Set<Color> colors = new HashSet<>();

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<ProductImage> images = new HashSet<>();

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<Offer> offers = new HashSet<>();


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
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }


}