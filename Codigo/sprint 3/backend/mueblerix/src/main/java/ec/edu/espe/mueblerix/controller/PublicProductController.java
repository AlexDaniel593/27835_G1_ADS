package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.ProductResponse;
import ec.edu.espe.mueblerix.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Productos Públicos", description = "Endpoints públicos para consultar productos (sin autenticación)")
public class PublicProductController {

    private final ProductService productService;

    /**
     * REQ009-1: Endpoint público para obtener todos los productos activos
     * @return Lista de productos activos
     */
    @GetMapping
    @Operation(summary = "Listar productos (público)", description = "Obtiene todos los productos activos sin autenticación")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente")
    })
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
    @Operation(summary = "Filtrar productos (público)", description = "Realiza una búsqueda de productos con múltiples filtros sin autenticación")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Búsqueda completada exitosamente")
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> filterProducts(
            @Parameter(description = "Nombre del producto") @RequestParam(required = false) String name,
            @Parameter(description = "ID de categoría") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "ID de material") @RequestParam(required = false) Long materialId,
            @Parameter(description = "ID de color") @RequestParam(required = false) Long colorId,
            @Parameter(description = "Precio mínimo") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Precio máximo") @RequestParam(required = false) BigDecimal maxPrice) {
        
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
    @Operation(summary = "Obtener producto por ID (público)", description = "Obtiene los detalles de un producto específico sin autenticación")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto obtenido exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<ApiResponse<ProductResponse>> getPublicProductById(
            @Parameter(description = "ID del producto", required = true) @PathVariable Long id) {
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
    @Operation(summary = "Obtener productos por categoría (público)", description = "Lista todos los productos de una categoría específica sin autenticación")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente")
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getPublicProductsByCategory(
            @Parameter(description = "ID de la categoría", required = true) @PathVariable Long categoryId) {
        log.info("Fetching public products by category ID: {}", categoryId);
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }
}
