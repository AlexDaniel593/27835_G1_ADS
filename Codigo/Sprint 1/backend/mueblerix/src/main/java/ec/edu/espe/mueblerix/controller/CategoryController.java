package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateCategoryRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.CategoryResponse;
import ec.edu.espe.mueblerix.service.product.CategoryService;
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
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("Fetching all categories");
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categorías obtenidas exitosamente", categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        log.info("Fetching category with ID: {}", id);
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Categoría obtenida exitosamente", category));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        log.info("Creating category: {}", request.getName());
        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Categoría creada exitosamente", category));
    }
}
