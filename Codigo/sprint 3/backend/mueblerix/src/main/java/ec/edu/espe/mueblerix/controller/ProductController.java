package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateProductRequest;
import ec.edu.espe.mueblerix.dto.request.UpdateProductRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.ProductResponse;
import ec.edu.espe.mueblerix.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Productos", description = "Gestión de productos del catálogo")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Crear nuevo producto", description = "Registra un nuevo producto en el sistema con sus detalles, imágenes, colores y materiales")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo producto", required = true)
            @Valid @RequestBody CreateProductRequest request) {
        log.info("Creating product: {}", request.getName());
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Producto creado exitosamente", product));
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Obtiene la lista completa de productos activos")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        log.info("Fetching all products");
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene los detalles completos de un producto específico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(description = "ID del producto", required = true) @PathVariable Long id) {
        log.info("Fetching product with ID: {}", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Producto obtenido exitosamente", product));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Obtener productos por categoría", description = "Lista todos los productos de una categoría específica")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Productos obtenidos exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "ID de la categoría", required = true) @PathVariable Long categoryId) {
        log.info("Fetching products by category ID: {}", categoryId);
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Productos obtenidos exitosamente", products));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar productos por nombre", description = "Realiza una búsqueda de productos por nombre")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Búsqueda completada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProductsByName(
            @Parameter(description = "Nombre o parte del nombre del producto", required = true) @RequestParam String name) {
        log.info("Searching products with name: {}", name);
        List<ProductResponse> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(ApiResponse.success("Búsqueda completada exitosamente", products));
    }

    @GetMapping("/search/advanced")
    @Operation(summary = "Búsqueda avanzada de productos", description = "Realiza una búsqueda de productos con múltiples filtros combinables")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Búsqueda avanzada completada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProductsAdvanced(
            @Parameter(description = "Nombre del producto") @RequestParam(required = false) String name,
            @Parameter(description = "ID de categoría") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "ID de material") @RequestParam(required = false) Long materialId,
            @Parameter(description = "ID de color") @RequestParam(required = false) Long colorId,
            @Parameter(description = "Precio mínimo") @RequestParam(required = false) java.math.BigDecimal minPrice,
            @Parameter(description = "Precio máximo") @RequestParam(required = false) java.math.BigDecimal maxPrice) {
        log.info("Advanced search - name: {}, categoryId: {}, materialId: {}, colorId: {}, price range: {}-{}", 
                name, categoryId, materialId, colorId, minPrice, maxPrice);
        List<ProductResponse> products = productService.searchProductsAdvanced(
                name, categoryId, materialId, colorId, minPrice, maxPrice);
        return ResponseEntity.ok(ApiResponse.success("Búsqueda avanzada completada exitosamente", products));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Realiza un borrado lógico del producto (lo marca como eliminado)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "ID del producto", required = true) @PathVariable Long id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal 
            ec.edu.espe.mueblerix.security.UserDetailsImpl userDetails) {
        log.info("Deleting product with ID: {} by user: {}", id, userDetails.getId());
        productService.deleteProduct(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("El producto ha sido eliminado correctamente", null));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(description = "ID del producto", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del producto", required = true)
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Producto actualizado correctamente", product));
    }

    // RF-07: Endpoints para Restaurar Producto

    @GetMapping("/deleted")
    @Operation(summary = "Listar productos eliminados", description = "Obtiene todos los productos que han sido eliminados lógicamente (bandeja de eliminados)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente (puede estar vacía)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getDeletedProducts() {
        log.info("Fetching all deleted products");
        List<ProductResponse> deletedProducts = productService.getDeletedProducts();
        
        // Excepción E.3: Si no existen productos eliminados
        if (deletedProducts.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No hay productos en la bandeja.", deletedProducts));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Productos eliminados obtenidos exitosamente", deletedProducts));
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "Restaurar producto eliminado", description = "Restaura un producto previamente eliminado a su estado activo")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto restaurado correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<ProductResponse>> restoreProduct(
            @Parameter(description = "ID del producto a restaurar", required = true) @PathVariable Long id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal 
            ec.edu.espe.mueblerix.security.UserDetailsImpl userDetails) {
        log.info("Restoring product with ID: {} by user: {}", id, userDetails.getId());
        ProductResponse product = productService.restoreProduct(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Producto restaurado correctamente.", product));
    }
}
