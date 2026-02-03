package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateProformaRequest;
import ec.edu.espe.mueblerix.dto.request.UpdateProformaRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.ProformaResponse;
import ec.edu.espe.mueblerix.service.proforma.ProformaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/proformas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Proformas", description = "Gestión de proformas y cotizaciones")
@SecurityRequirement(name = "bearerAuth")
public class ProformaController {

    private final ProformaService proformaService;

    /**
     * REQ010-2: Crear proforma
     */
    @PostMapping
    @Operation(summary = "Crear nueva proforma", description = "Genera una nueva proforma con los datos del cliente y los productos seleccionados")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Proforma generada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<ProformaResponse>> createProforma(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la nueva proforma", required = true)
            @Valid @RequestBody CreateProformaRequest request,
            @AuthenticationPrincipal ec.edu.espe.mueblerix.security.UserDetailsImpl userDetails) {
        
        log.info("Creating proforma for customer: {}", request.getCustomer().getIdentification());
        ProformaResponse proforma = proformaService.createProforma(request, userDetails.getId());
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Proforma generada exitosamente.", proforma));
    }

    /**
     * Obtener todas las proformas
     */
    @GetMapping
    @Operation(summary = "Listar todas las proformas", description = "Obtiene la lista completa de proformas registradas")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Proformas obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProformaResponse>>> getAllProformas() {
        log.info("Fetching all proformas");
        List<ProformaResponse> proformas = proformaService.getAllProformas();
        return ResponseEntity.ok(ApiResponse.success("Proformas obtenidas exitosamente", proformas));
    }

    /**
     * Obtener proforma por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener proforma por ID", description = "Obtiene los detalles completos de una proforma específica")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Proforma obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Proforma no encontrada", content = @Content)
    })
    public ResponseEntity<ApiResponse<ProformaResponse>> getProformaById(
            @Parameter(description = "ID de la proforma", required = true) @PathVariable Long id) {
        log.info("Fetching proforma with ID: {}", id);
        ProformaResponse proforma = proformaService.getProformaById(id);
        return ResponseEntity.ok(ApiResponse.success("Proforma obtenida exitosamente", proforma));
    }

    /**
     * REQ012-1: Buscar proformas con filtros
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar proformas", description = "Realiza una búsqueda de proformas con filtros múltiples (código, cliente, fechas)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Búsqueda completada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<ProformaResponse>>> searchProformas(
            @Parameter(description = "Código de proforma") @RequestParam(required = false) String code,
            @Parameter(description = "Nombre del cliente") @RequestParam(required = false) String customerName,
            @Parameter(description = "Identificación del cliente") @RequestParam(required = false) String customerIdentification,
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd'T'HH:mm:ss)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd'T'HH:mm:ss)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Searching proformas with filters");
        List<ProformaResponse> proformas = proformaService.searchProformas(
                code, customerName, customerIdentification, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.success(
                proformas.isEmpty() ? "No se encontraron proformas con los criterios ingresados" : "Proformas encontradas", 
                proformas));
    }

    /**
     * REQ011-1: Actualizar proforma
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proforma", description = "Modifica los datos de una proforma existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Proforma actualizada correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Proforma no encontrada", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<ApiResponse<ProformaResponse>> updateProforma(
            @Parameter(description = "ID de la proforma", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados de la proforma", required = true)
            @Valid @RequestBody UpdateProformaRequest request,
            @AuthenticationPrincipal ec.edu.espe.mueblerix.security.UserDetailsImpl userDetails) {
        
        log.info("Updating proforma ID: {}", id);
        ProformaResponse proforma = proformaService.updateProforma(id, request, userDetails.getId());
        
        return ResponseEntity.ok(ApiResponse.success("Proforma actualizada correctamente", proforma));
    }
}
