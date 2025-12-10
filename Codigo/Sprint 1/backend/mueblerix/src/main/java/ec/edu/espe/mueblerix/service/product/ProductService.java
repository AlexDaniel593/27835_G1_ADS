package ec.edu.espe.mueblerix.service.product;

import ec.edu.espe.mueblerix.dto.request.CreateProductRequest;
import ec.edu.espe.mueblerix.dto.response.*;
import ec.edu.espe.mueblerix.model.*;
import ec.edu.espe.mueblerix.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MaterialRepository materialRepository;
    private final ColorRepository colorRepository;
    private final ProductImageRepository productImageRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        try {
            // Validar categoría
            Category category = categoryRepository.findByIdAndIsActiveTrue(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada o inactiva"));

            // Crear el producto con colecciones inicializadas
            Product product = Product.builder()
                    .name(request.getName())
                    .price(request.getPrice())
                    .category(category)
                    .materials(new HashSet<>())
                    .colors(new HashSet<>())
                    .images(new HashSet<>())
                    .isActive(true)
                    .isDeleted(false)
                    .build();

            // Agregar materiales si existen
            if (request.getMaterialIds() != null && !request.getMaterialIds().isEmpty()) {
                Set<Material> materials = new HashSet<>(
                        materialRepository.findByIdInAndIsActiveTrue(request.getMaterialIds())
                );
                if (materials.size() != request.getMaterialIds().size()) {
                    throw new RuntimeException("Algunos materiales no fueron encontrados o están inactivos");
                }
                product.setMaterials(materials);
            }

            // Agregar colores si existen
            if (request.getColorIds() != null && !request.getColorIds().isEmpty()) {
                Set<Color> colors = new HashSet<>(
                        colorRepository.findByIdInAndIsActiveTrue(request.getColorIds())
                );
                if (colors.size() != request.getColorIds().size()) {
                    throw new RuntimeException("Algunos colores no fueron encontrados o están inactivos");
                }
                product.setColors(colors);
            }

            // Guardar el producto
            Product savedProduct = productRepository.save(product);

            // Agregar imágenes si existen
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                Set<ProductImage> images = new HashSet<>();
                for (int i = 0; i < request.getImageUrls().size(); i++) {
                    ProductImage image = ProductImage.builder()
                            .product(savedProduct)
                            .url(request.getImageUrls().get(i))
                            .order(i)
                            .isPrimary(i == 0) // La primera imagen es la principal
                            .build();
                    images.add(productImageRepository.save(image));
                }
                savedProduct.setImages(images);
            }

            log.info("Product created successfully with ID: {}", savedProduct.getId());
            return mapToProductResponse(savedProduct);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear el producto: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all active products");
        List<Product> products = productRepository.findByIsActiveTrueAndIsDeletedFalse();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return mapToProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        log.info("Fetching products by category ID: {}", categoryId);
        List<Product> products = productRepository.findActiveByCategoryId(categoryId);
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        List<Product> products = productRepository.searchByName(name);
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .category(mapToCategoryResponse(product.getCategory()))
                .materials(product.getMaterials() != null ? product.getMaterials().stream()
                        .map(this::mapToMaterialResponse)
                        .collect(Collectors.toList()) : List.of())
                .colors(product.getColors() != null ? product.getColors().stream()
                        .map(this::mapToColorResponse)
                        .collect(Collectors.toList()) : List.of())
                .images(product.getImages() != null ? product.getImages().stream()
                        .map(this::mapToProductImageResponse)
                        .sorted((i1, i2) -> i1.getOrder().compareTo(i2.getOrder()))
                        .collect(Collectors.toList()) : List.of())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .build();
    }

    private MaterialResponse mapToMaterialResponse(Material material) {
        return MaterialResponse.builder()
                .id(material.getId())
                .name(material.getName())
                .description(material.getDescription())
                .isActive(material.getIsActive())
                .build();
    }

    private ColorResponse mapToColorResponse(Color color) {
        return ColorResponse.builder()
                .id(color.getId())
                .name(color.getName())
                .isActive(color.getIsActive())
                .build();
    }

    private ProductImageResponse mapToProductImageResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .url(image.getUrl())
                .order(image.getOrder())
                .isPrimary(image.getIsPrimary())
                .uploadedAt(image.getUploadedAt())
                .build();
    }
}
