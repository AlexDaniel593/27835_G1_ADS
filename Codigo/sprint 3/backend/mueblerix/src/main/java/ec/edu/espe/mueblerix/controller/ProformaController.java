package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateProformaRequest;
import ec.edu.espe.mueblerix.dto.request.UpdateProformaRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.ProformaResponse;
import ec.edu.espe.mueblerix.service.proforma.ProformaService;
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
public class ProformaController {

    private final ProformaService proformaService;

    /**
     * REQ010-2: Crear proforma
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProformaResponse>> createProforma(
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
    public ResponseEntity<ApiResponse<List<ProformaResponse>>> getAllProformas() {
        log.info("Fetching all proformas");
        List<ProformaResponse> proformas = proformaService.getAllProformas();
        return ResponseEntity.ok(ApiResponse.success("Proformas obtenidas exitosamente", proformas));
    }

    /**
     * Obtener proforma por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProformaResponse>> getProformaById(@PathVariable Long id) {
        log.info("Fetching proforma with ID: {}", id);
        ProformaResponse proforma = proformaService.getProformaById(id);
        return ResponseEntity.ok(ApiResponse.success("Proforma obtenida exitosamente", proforma));
    }

    /**
     * REQ012-1: Buscar proformas con filtros
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProformaResponse>>> searchProformas(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerIdentification,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
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
    public ResponseEntity<ApiResponse<ProformaResponse>> updateProforma(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProformaRequest request,
            @AuthenticationPrincipal ec.edu.espe.mueblerix.security.UserDetailsImpl userDetails) {
        
        log.info("Updating proforma ID: {}", id);
        ProformaResponse proforma = proformaService.updateProforma(id, request, userDetails.getId());
        
        return ResponseEntity.ok(ApiResponse.success("Proforma actualizada correctamente", proforma));
    }
}
