package ec.edu.espe.mueblerix.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 10)
  private String identification;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, length = 100)
  private String firstName;

  @Column(length = 100)
  private String lastName;

  @Column(length = 100)
  private String email;

  @Column(length = 15)
  private String phone;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isBlocked = false;

  @Column(nullable = false)
  @Builder.Default
  private Boolean firstLogin = true;

  @Column(name = "failed_login_attempts", nullable = false)
  @Builder.Default
  private Integer failedLoginAttempts = 0;

  @Column
  private LocalDateTime blockedAt;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime lastAccessAt;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "user_roles",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  @OneToMany(mappedBy = "creationUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Offer> createdOffers = new HashSet<>();

  @OneToMany(mappedBy = "deletionUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Product> deletedProducts = new HashSet<>();

  @OneToMany(mappedBy = "creationUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Proforma> createdProformas = new HashSet<>();

  @OneToMany(mappedBy = "updateUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Proforma> updatedProformas = new HashSet<>();

  @OneToMany(mappedBy = "deletionUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Proforma> deletedProformas = new HashSet<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Builder.Default
  private Set<ProformaModificationHistory> proformaModifications = new HashSet<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Report> generatedReports = new HashSet<>();

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }


}
