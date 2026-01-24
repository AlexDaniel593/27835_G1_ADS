package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.ProductResponse;
import ec.edu.espe.mueblerix.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public/products")
@RequiredArgsConstructor
@Slf4j
public class PublicProductController {

    private final ProductService productService;

    /**
     * REQ009-1: Endpoint público para obtener todos los productos activos
     * @return Lista de productos activos
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getPublicProducts() {
        log.info("Fetching all public products");
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    /**
     * REQ009-2: Endpoint público con filtros básicos
     * @param name Filtro por nombre del producto
     * @param categoryId Filtro por ID de categoría
     * @param materialId Filtro por ID de material
     * @param colorId Filtro por ID de color
     * @param minPrice Precio mínimo
     * @param maxPrice Precio máximo
     * @return Lista de productos filtrados
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> filterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) Long colorId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        
        log.info("Filtering public products - name: {}, categoryId: {}, materialId: {}, colorId: {}, price range: {}-{}", 
                name, categoryId, materialId, colorId, minPrice, maxPrice);
        
        List<ProductResponse> products = productService.searchProductsAdvanced(
                name, categoryId, materialId, colorId, minPrice, maxPrice);
        
        return ResponseEntity.ok(ApiResponse.success("Búsqueda completada exitosamente", products));
    }

    /**
     * REQ009-1: Obtener producto específico por ID (público)
     * @param id ID del producto
     * @return Detalles del producto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getPublicProductById(@PathVariable Long id) {
        log.info("Fetching public product with ID: {}", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Producto obtenido exitosamente", product));
    }

    /**
     * REQ009-2: Obtener productos por categoría (público)
     * @param categoryId ID de la categoría
     * @return Lista de productos de la categoría
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getPublicProductsByCategory(
            @PathVariable Long categoryId) {
        log.info("Fetching public products by category ID: {}", categoryId);
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }
}
