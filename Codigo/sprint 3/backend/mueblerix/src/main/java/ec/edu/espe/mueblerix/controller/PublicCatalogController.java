package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.CategoryResponse;
import ec.edu.espe.mueblerix.dto.response.ColorResponse;
import ec.edu.espe.mueblerix.dto.response.MaterialResponse;
import ec.edu.espe.mueblerix.service.product.CategoryService;
import ec.edu.espe.mueblerix.service.product.ColorService;
import ec.edu.espe.mueblerix.service.product.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REQ009: Controlador público para catálogos (categorías, materiales, colores)
 * Permite acceso sin autenticación para el catálogo público
 */
@RestController
@RequestMapping("/api/v1/public/catalog")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Catálogo Público", description = "Endpoints públicos para acceder a catálogos (sin autenticación)")
public class PublicCatalogController {

    private final CategoryService categoryService;
    private final MaterialService materialService;
    private final ColorService colorService;

    /**
     * Obtener todas las categorías (público)
     */
    @GetMapping("/categories")
    @Operation(summary = "Listar categorías (público)", description = "Obtiene todas las categorías disponibles sin autenticación")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categorías obtenidas exitosamente")
    })
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("Fetching all categories for public catalog");
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categorías obtenidas exitosamente", categories));
    }

    /**
     * Obtener todos los materiales (público)
     */
    @GetMapping("/materials")
    @Operation(summary = "Listar materiales (público)", description = "Obtiene todos los materiales disponibles sin autenticación")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Materiales obtenidos exitosamente")
    })
    public ResponseEntity<ApiResponse<List<MaterialResponse>>> getAllMaterials() {
        log.info("Fetching all materials for public catalog");
        List<MaterialResponse> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(ApiResponse.success("Materiales obtenidos exitosamente", materials));
    }

    /**
     * Obtener todos los colores (público)
     */
    @GetMapping("/colors")
    @Operation(summary = "Listar colores (público)", description = "Obtiene todos los colores disponibles sin autenticación")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Colores obtenidos exitosamente")
    })
    public ResponseEntity<ApiResponse<List<ColorResponse>>> getAllColors() {
        log.info("Fetching all colors for public catalog");
        List<ColorResponse> colors = colorService.getAllColors();
        return ResponseEntity.ok(ApiResponse.success("Colores obtenidos exitosamente", colors));
    }
}
