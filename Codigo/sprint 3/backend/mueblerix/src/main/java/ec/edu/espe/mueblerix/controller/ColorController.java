package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateColorRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.ColorResponse;
import ec.edu.espe.mueblerix.service.product.ColorService;
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
@RequestMapping("/api/v1/colors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Colores", description = "Gestión de colores de productos")
@SecurityRequirement(name = "bearerAuth")
public class ColorController {

    private final ColorService colorService;

    @GetMapping
    @Operation(summary = "Listar todos los colores", description = "Obtiene la lista completa de colores disponibles")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de colores obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ColorResponse>>> getAllColors() {
        log.info("Fetching all colors");
        List<ColorResponse> colors = colorService.getAllColors();
        return ResponseEntity.ok(ApiResponse.success("Colores obtenidos exitosamente", colors));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener color por ID", description = "Obtiene los detalles de un color específico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Color encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Color no encontrado", content = @Content)
    })
    public ResponseEntity<ApiResponse<ColorResponse>> getColorById(
            @Parameter(description = "ID del color", required = true) @PathVariable Long id) {
        log.info("Fetching color with ID: {}", id);
        ColorResponse color = colorService.getColorById(id);
        return ResponseEntity.ok(ApiResponse.success("Color obtenido exitosamente", color));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo color", description = "Registra un nuevo color en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Color creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<ApiResponse<ColorResponse>> createColor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo color", required = true)
            @Valid @RequestBody CreateColorRequest request) {
        log.info("Creating color: {}", request.getName());
        ColorResponse color = colorService.createColor(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Color creado exitosamente", color));
    }
}
