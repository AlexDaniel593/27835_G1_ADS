package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.CategoryResponse;
import ec.edu.espe.mueblerix.dto.response.ColorResponse;
import ec.edu.espe.mueblerix.dto.response.MaterialResponse;
import ec.edu.espe.mueblerix.service.product.CategoryService;
import ec.edu.espe.mueblerix.service.product.ColorService;
import ec.edu.espe.mueblerix.service.product.MaterialService;
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
public class PublicCatalogController {

    private final CategoryService categoryService;
    private final MaterialService materialService;
    private final ColorService colorService;

    /**
     * Obtener todas las categorías (público)
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("Fetching all categories for public catalog");
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categorías obtenidas exitosamente", categories));
    }

    /**
     * Obtener todos los materiales (público)
     */
    @GetMapping("/materials")
    public ResponseEntity<ApiResponse<List<MaterialResponse>>> getAllMaterials() {
        log.info("Fetching all materials for public catalog");
        List<MaterialResponse> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(ApiResponse.success("Materiales obtenidos exitosamente", materials));
    }

    /**
     * Obtener todos los colores (público)
     */
    @GetMapping("/colors")
    public ResponseEntity<ApiResponse<List<ColorResponse>>> getAllColors() {
        log.info("Fetching all colors for public catalog");
        List<ColorResponse> colors = colorService.getAllColors();
        return ResponseEntity.ok(ApiResponse.success("Colores obtenidos exitosamente", colors));
    }
}
