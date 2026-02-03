package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateMaterialRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.MaterialResponse;
import ec.edu.espe.mueblerix.service.product.MaterialService;
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
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Materiales", description = "Gestión de materiales de productos")
@SecurityRequirement(name = "bearerAuth")
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    @Operation(summary = "Listar todos los materiales", description = "Obtiene la lista completa de materiales disponibles")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de materiales obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<MaterialResponse>>> getAllMaterials() {
        log.info("Fetching all materials");
        List<MaterialResponse> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(ApiResponse.success("Materiales obtenidos exitosamente", materials));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener material por ID", description = "Obtiene los detalles de un material específico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Material encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Material no encontrado", content = @Content)
    })
    public ResponseEntity<ApiResponse<MaterialResponse>> getMaterialById(
            @Parameter(description = "ID del material", required = true) @PathVariable Long id) {
        log.info("Fetching material with ID: {}", id);
        MaterialResponse material = materialService.getMaterialById(id);
        return ResponseEntity.ok(ApiResponse.success("Material obtenido exitosamente", material));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo material", description = "Registra un nuevo material en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Material creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<ApiResponse<MaterialResponse>> createMaterial(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo material", required = true)
            @Valid @RequestBody CreateMaterialRequest request) {
        log.info("Creating material: {}", request.getName());
        MaterialResponse material = materialService.createMaterial(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Material creado exitosamente", material));
    }
}
