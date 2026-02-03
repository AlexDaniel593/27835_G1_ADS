package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateCategoryRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.CategoryResponse;
import ec.edu.espe.mueblerix.service.product.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Listar todas las categorías", description = "Obtiene la lista completa de categorías disponibles")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("Fetching all categories");
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categorías obtenidas exitosamente", categories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Obtiene los detalles de una categoría específica")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @Parameter(description = "ID de la categoría", required = true) @PathVariable Long id) {
        log.info("Fetching category with ID: {}", id);
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Categoría obtenida exitosamente", category));
    }

    @PostMapping
    @Operation(summary = "Crear nueva categoría", description = "Registra una nueva categoría en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la nueva categoría", required = true)
            @Valid @RequestBody CreateCategoryRequest request) {
        log.info("Creating category: {}", request.getName());
        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Categoría creada exitosamente", category));
    }
}
