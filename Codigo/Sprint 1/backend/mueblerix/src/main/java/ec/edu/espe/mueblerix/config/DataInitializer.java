package ec.edu.espe.mueblerix.config;

import ec.edu.espe.mueblerix.model.*;
import ec.edu.espe.mueblerix.model.enums.RoleName;
import ec.edu.espe.mueblerix.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final CategoryRepository categoryRepository;
  private final MaterialRepository materialRepository;
  private final ColorRepository colorRepository;

  @Bean
  public CommandLineRunner initData() {
    return args -> {
      log.info("Iniciando carga de datos iniciales...");

      // Crear roles si no existen
      Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
              .orElseGet(() -> {
                Role role = Role.builder()
                        .name(RoleName.ROLE_ADMIN)
                        .description("Administrador del sistema")
                        .build();
                return roleRepository.save(role);
              });

      roleRepository.findByName(RoleName.ROLE_EMPLOYEE)
              .orElseGet(() -> {
                Role role = Role.builder()
                        .name(RoleName.ROLE_EMPLOYEE)
                        .description("Empleado del sistema")
                        .build();
                return roleRepository.save(role);
              });

      log.info("Roles creados: ROLE_ADMIN, ROLE_EMPLOYEE");

      // Crear categorías si no existen
      createCategoryIfNotExists("Puerta", "Puertas de madera y MDF para interiores y exteriores");
      createCategoryIfNotExists("Mueble De Cocina", "Muebles modulares y personalizados para cocinas");
      createCategoryIfNotExists("Closet", "Closets y armarios a medida");
      log.info("Categorías iniciales creadas");

      // Crear materiales si no existen
      createMaterialIfNotExists("Madera", "Madera natural de alta calidad");
      createMaterialIfNotExists("MDF", "Medium Density Fiberboard - Material compuesto");
      createMaterialIfNotExists("Melaminico", "Tablero recubierto con melamina");
      log.info("Materiales iniciales creados");

      // Crear colores si no existen
      createColorIfNotExists("Caramelo");
      createColorIfNotExists("Wengue");
      createColorIfNotExists("Cedro");
      createColorIfNotExists("Blanco");
      log.info("Colores iniciales creados");

      userRepository.findByIdentification("1234567890")
              .orElseGet(() -> {
                User adminUser = User.builder()
                        .identification("1234567890")
                        .password(passwordEncoder.encode("Admin123!"))
                        .firstName("Administrador")
                        .lastName("Sistema")
                        .email("admin@mueblerix.com")
                        .phone("0999999999")
                        .isActive(true)
                        .isBlocked(false)
                        .firstLogin(true)
                        .failedLoginAttempts(0)
                        .roles(Set.of(adminRole))
                        .build();

                User savedUser = userRepository.save(adminUser);
                log.info("Usuario administrador creado exitosamente - Identificación: 1234567890, Password: Admin123!");
                return savedUser;
              });

      log.info("Carga de datos iniciales completada");
    };
  }

  private void createCategoryIfNotExists(String name, String description) {
    categoryRepository.findByName(name)
            .orElseGet(() -> {
              Category category = Category.builder()
                      .name(name)
                      .description(description)
                      .isActive(true)
                      .build();
              return categoryRepository.save(category);
            });
  }

  private void createMaterialIfNotExists(String name, String description) {
    materialRepository.findByName(name)
            .orElseGet(() -> {
              Material material = Material.builder()
                      .name(name)
                      .description(description)
                      .isActive(true)
                      .build();
              return materialRepository.save(material);
            });
  }

  private void createColorIfNotExists(String name) {
    colorRepository.findByName(name)
            .orElseGet(() -> {
              Color color = Color.builder()
                      .name(name)
                      .isActive(true)
                      .build();
              return colorRepository.save(color);
            });
  }
}
