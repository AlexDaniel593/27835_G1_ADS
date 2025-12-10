package ec.edu.espe.mueblerix.service.product;

import ec.edu.espe.mueblerix.dto.request.CreateCategoryRequest;
import ec.edu.espe.mueblerix.dto.response.CategoryResponse;
import ec.edu.espe.mueblerix.model.Category;
import ec.edu.espe.mueblerix.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching all active categories");
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        log.info("Fetching category with ID: {}", id);
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return mapToCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating new category: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return mapToCategoryResponse(savedCategory);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .build();
    }
}
