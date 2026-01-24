package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateOfferRequest;
import ec.edu.espe.mueblerix.dto.request.UpdateOfferRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.OfferResponse;
import ec.edu.espe.mueblerix.service.offer.OfferService;
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
public class OfferController {

    private final OfferService offerService;

    // Paso 2: Obtener lista de ofertas actuales (activas y próximas)
    @GetMapping
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getAllOffers() {
        log.info("Fetching all offers");
        List<OfferResponse> offers = offerService.getAllOffers();
        return ResponseEntity.ok(ApiResponse.success("Ofertas obtenidas exitosamente", offers));
    }

    // Obtener oferta por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OfferResponse>> getOfferById(@PathVariable Long id) {
        log.info("Fetching offer with ID: {}", id);
        OfferResponse offer = offerService.getOfferById(id);
        return ResponseEntity.ok(ApiResponse.success("Oferta obtenida exitosamente", offer));
    }

    // Obtener ofertas por producto
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getOffersByProductId(@PathVariable Long productId) {
        log.info("Fetching offers for product ID: {}", productId);
        List<OfferResponse> offers = offerService.getOffersByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Ofertas del producto obtenidas exitosamente", offers));
    }

    // Flujo Alterno A - Registrar nueva oferta
    @PostMapping
    public ResponseEntity<ApiResponse<OfferResponse>> createOffer(
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
    public ResponseEntity<ApiResponse<OfferResponse>> updateOffer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOfferRequest request) {
        log.info("Updating offer with ID: {}", id);
        OfferResponse offer = offerService.updateOffer(id, request);
        // B7: Mensaje de éxito
        return ResponseEntity.ok(ApiResponse.success("Oferta modificada correctamente.", offer));
    }

    // Flujo Alterno C - Eliminar oferta
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOffer(@PathVariable Long id) {
        log.info("Deleting offer with ID: {}", id);
        offerService.deleteOffer(id);
        // C5: Mensaje de éxito
        return ResponseEntity.ok(ApiResponse.success("Oferta eliminada correctamente.", null));
    }
}
