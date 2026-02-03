package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateOfferRequest;
import ec.edu.espe.mueblerix.dto.request.UpdateOfferRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.OfferResponse;
import ec.edu.espe.mueblerix.service.offer.OfferService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ofertas", description = "Gestión de ofertas y promociones")
@SecurityRequirement(name = "bearerAuth")
public class OfferController {

    private final OfferService offerService;

    // Paso 2: Obtener lista de ofertas actuales (activas y próximas)
    @GetMapping
    @Operation(summary = "Listar todas las ofertas", description = "Obtiene la lista de ofertas actuales (activas y próximas)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ofertas obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getAllOffers() {
        log.info("Fetching all offers");
        List<OfferResponse> offers = offerService.getAllOffers();
        return ResponseEntity.ok(ApiResponse.success("Ofertas obtenidas exitosamente", offers));
    }

    // Obtener oferta por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener oferta por ID", description = "Obtiene los detalles de una oferta específica")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Oferta encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content)
    })
    public ResponseEntity<ApiResponse<OfferResponse>> getOfferById(
            @Parameter(description = "ID de la oferta", required = true) @PathVariable Long id) {
        log.info("Fetching offer with ID: {}", id);
        OfferResponse offer = offerService.getOfferById(id);
        return ResponseEntity.ok(ApiResponse.success("Oferta obtenida exitosamente", offer));
    }

    // Obtener ofertas por producto
    @GetMapping("/product/{productId}")
    @Operation(summary = "Obtener ofertas de un producto", description = "Lista todas las ofertas asociadas a un producto específico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ofertas del producto obtenidas exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getOffersByProductId(
            @Parameter(description = "ID del producto", required = true) @PathVariable Long productId) {
        log.info("Fetching offers for product ID: {}", productId);
        List<OfferResponse> offers = offerService.getOffersByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Ofertas del producto obtenidas exitosamente", offers));
    }

    // Flujo Alterno A - Registrar nueva oferta
    @PostMapping
    @Operation(summary = "Crear nueva oferta", description = "Registra una nueva oferta de descuento para un producto")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Oferta registrada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<ApiResponse<OfferResponse>> createOffer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la nueva oferta", required = true)
            @Valid @RequestBody CreateOfferRequest request,
            @AuthenticationPrincipal ec.edu.espe.mueblerix.security.UserDetailsImpl userDetails) {
        log.info("Creating offer for product ID: {} by user: {}", request.getProductId(), userDetails.getId());
        OfferResponse offer = offerService.createOffer(request, userDetails.getId());
        // A7: Mensaje de éxito
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Oferta registrada exitosamente.", offer));
    }

    // Flujo Alterno B - Modificar oferta existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar oferta", description = "Modifica los datos de una oferta existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Oferta modificada correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<ApiResponse<OfferResponse>> updateOffer(
            @Parameter(description = "ID de la oferta", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados de la oferta", required = true)
            @Valid @RequestBody UpdateOfferRequest request) {
        log.info("Updating offer with ID: {}", id);
        OfferResponse offer = offerService.updateOffer(id, request);
        // B7: Mensaje de éxito
        return ResponseEntity.ok(ApiResponse.success("Oferta modificada correctamente.", offer));
    }

    // Flujo Alterno C - Eliminar oferta
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar oferta", description = "Elimina una oferta del sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Oferta eliminada correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Oferta no encontrada", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> deleteOffer(
            @Parameter(description = "ID de la oferta", required = true) @PathVariable Long id) {
        log.info("Deleting offer with ID: {}", id);
        offerService.deleteOffer(id);
        // C5: Mensaje de éxito
        return ResponseEntity.ok(ApiResponse.success("Oferta eliminada correctamente.", null));
    }
}
