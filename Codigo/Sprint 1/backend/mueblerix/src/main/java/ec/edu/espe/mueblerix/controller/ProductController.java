package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateProductRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.ProductResponse;
import ec.edu.espe.mueblerix.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        log.info("Creating product: {}", request.getName());
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Producto creado exitosamente", product));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        log.info("Fetching all products");
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        log.info("Fetching product with ID: {}", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Producto obtenido exitosamente", product));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @PathVariable Long categoryId) {
        log.info("Fetching products by category ID: {}", categoryId);
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProductsByName(
            @RequestParam String name) {
        log.info("Searching products with name: {}", name);
        List<ProductResponse> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(ApiResponse.success("Búsqueda completada exitosamente", products));
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProductsAdvanced(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) Long colorId,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice) {
        log.info("Advanced search - name: {}, categoryId: {}, materialId: {}, colorId: {}, price range: {}-{}", 
                name, categoryId, materialId, colorId, minPrice, maxPrice);
        List<ProductResponse> products = productService.searchProductsAdvanced(
                name, categoryId, materialId, colorId, minPrice, maxPrice);
        return ResponseEntity.ok(ApiResponse.success("Búsqueda avanzada completada exitosamente", products));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Producto eliminado exitosamente", null));
    }
}
