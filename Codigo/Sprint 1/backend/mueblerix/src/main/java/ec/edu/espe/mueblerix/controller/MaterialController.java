package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateMaterialRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.MaterialResponse;
import ec.edu.espe.mueblerix.service.product.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@Slf4j
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaterialResponse>>> getAllMaterials() {
        log.info("Fetching all materials");
        List<MaterialResponse> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(ApiResponse.success("Materiales obtenidos exitosamente", materials));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialResponse>> getMaterialById(@PathVariable Long id) {
        log.info("Fetching material with ID: {}", id);
        MaterialResponse material = materialService.getMaterialById(id);
        return ResponseEntity.ok(ApiResponse.success("Material obtenido exitosamente", material));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MaterialResponse>> createMaterial(
            @Valid @RequestBody CreateMaterialRequest request) {
        log.info("Creating material: {}", request.getName());
        MaterialResponse material = materialService.createMaterial(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Material creado exitosamente", material));
    }
}
