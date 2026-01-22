package ec.edu.espe.mueblerix.service.offer;

import ec.edu.espe.mueblerix.dto.request.CreateOfferRequest;
import ec.edu.espe.mueblerix.dto.request.UpdateOfferRequest;
import ec.edu.espe.mueblerix.dto.response.OfferResponse;
import ec.edu.espe.mueblerix.model.Offer;
import ec.edu.espe.mueblerix.model.Product;
import ec.edu.espe.mueblerix.model.User;
import ec.edu.espe.mueblerix.model.enums.OfferType;
import ec.edu.espe.mueblerix.repository.OfferRepository;
import ec.edu.espe.mueblerix.repository.ProductRepository;
import ec.edu.espe.mueblerix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferService {

    private final OfferRepository offerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Flujo Alterno A - Registrar nueva oferta
    @Transactional
    public OfferResponse createOffer(CreateOfferRequest request, Long userId) {
        log.info("Creating new offer for product ID: {}", request.getProductId());

        try {
            // A5: Validar que el producto existe y está activo
            Product product = productRepository.findByIdAndIsActiveTrueAndIsDeletedFalse(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado o inactivo"));

            // A5: Validar rango de fechas (Excepción A5/B5)
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new RuntimeException("El rango de fechas no es válido.");
            }

            // A5: Validar que el producto no tenga otra oferta activa en el mismo periodo
            if (offerRepository.existsActiveOfferForProductInPeriod(
                    request.getProductId(), 
                    request.getStartDate(), 
                    request.getEndDate())) {
                throw new RuntimeException("El producto ya cuenta con una oferta activa en este periodo.");
            }

            // A5: Validar datos según el tipo de oferta
            validateOfferData(request.getType(), request.getDiscountValue(), 
                            request.getPromotionalPrice(), product.getPrice());

            // A6: Crear la oferta
            Offer offer = Offer.builder()
                    .product(product)
                    .type(request.getType())
                    .discountValue(request.getDiscountValue())
                    .promotionalPrice(request.getPromotionalPrice())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .isActive(request.getIsActive())
                    .build();

            // Registrar usuario creador
            if (userId != null) {
                User user = new User();
                user.setId(userId);
                offer.setCreationUser(user);
            }

            // Guardar la oferta
            Offer savedOffer = offerRepository.save(offer);
            log.info("Offer created successfully with ID: {}", savedOffer.getId());

            // A7: Retornar respuesta
            return mapToOfferResponse(savedOffer);

        } catch (Exception e) {
            log.error("Error creating offer: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // Paso 2: Obtener lista de ofertas actuales (activas y próximas)
    @Transactional(readOnly = true)
    public List<OfferResponse> getAllOffers() {
        log.info("Fetching all active offers");
        List<Offer> offers = offerRepository.findAllActiveOffers();
        return offers.stream()
                .map(this::mapToOfferResponse)
                .collect(Collectors.toList());
    }

    // Obtener oferta por ID
    @Transactional(readOnly = true)
    public OfferResponse getOfferById(Long id) {
        log.info("Fetching offer with ID: {}", id);
        Offer offer = offerRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));
        return mapToOfferResponse(offer);
    }

    // Obtener ofertas por producto
    @Transactional(readOnly = true)
    public List<OfferResponse> getOffersByProductId(Long productId) {
        log.info("Fetching offers for product ID: {}", productId);
        List<Offer> offers = offerRepository.findByProductId(productId);
        return offers.stream()
                .map(this::mapToOfferResponse)
                .collect(Collectors.toList());
    }

    // Flujo Alterno B - Modificar oferta existente
    @Transactional
    public OfferResponse updateOffer(Long id, UpdateOfferRequest request) {
        log.info("Updating offer with ID: {}", id);

        try {
            // B1-B2: Obtener la oferta existente
            Offer offer = offerRepository.findByIdAndIsActiveTrue(id)
                    .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

            // B5: Validar que el producto existe y está activo
            Product product = productRepository.findByIdAndIsActiveTrueAndIsDeletedFalse(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado o inactivo"));

            // B5: Validar rango de fechas
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new RuntimeException("El rango de fechas no es válido.");
            }

            // B5: Validar que no haya conflicto con otras ofertas (excluyendo esta)
            if (offerRepository.existsActiveOfferForProductInPeriodExcludingOffer(
                    request.getProductId(), 
                    id, 
                    request.getStartDate(), 
                    request.getEndDate())) {
                throw new RuntimeException("El producto ya cuenta con una oferta activa en este periodo.");
            }

            // B5: Validar datos según el tipo de oferta
            validateOfferData(request.getType(), request.getDiscountValue(), 
                            request.getPromotionalPrice(), product.getPrice());

            // B3-B4: Actualizar los datos
            offer.setProduct(product);
            offer.setType(request.getType());
            offer.setDiscountValue(request.getDiscountValue());
            offer.setPromotionalPrice(request.getPromotionalPrice());
            offer.setStartDate(request.getStartDate());
            offer.setEndDate(request.getEndDate());
            offer.setIsActive(request.getIsActive());

            // B6: Guardar cambios
            Offer updatedOffer = offerRepository.save(offer);
            log.info("Offer updated successfully with ID: {}", id);

            // B7: Retornar respuesta
            return mapToOfferResponse(updatedOffer);

        } catch (Exception e) {
            log.error("Error updating offer: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // Flujo Alterno C - Eliminar oferta
    @Transactional
    public void deleteOffer(Long id) {
        log.info("Deleting offer with ID: {}", id);

        try {
            // C1: Verificar que la oferta existe
            Offer offer = offerRepository.findByIdAndIsActiveTrue(id)
                    .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

            // C4: Eliminar la oferta (soft delete)
            offer.setIsActive(false);
            offerRepository.save(offer);
            
            log.info("Offer deleted successfully with ID: {}", id);

        } catch (Exception e) {
            log.error("Error deleting offer: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // Validaciones
    private void validateOfferData(OfferType type, BigDecimal discountValue, 
                                  BigDecimal promotionalPrice, BigDecimal originalPrice) {
        
        if (type == OfferType.PERCENTAGE_DISCOUNT) {
            // Validar descuento porcentual
            if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Datos inválidos. Verifique la oferta ingresada.");
            }
            if (discountValue.compareTo(new BigDecimal("100")) > 0) {
                throw new RuntimeException("El descuento no puede ser mayor al 100%");
            }
            
            // Validar que el precio final no sea negativo
            BigDecimal finalPrice = calculateFinalPrice(originalPrice, type, discountValue, null);
            if (finalPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("El precio final no puede ser negativo o cero");
            }
            
        } else if (type == OfferType.PROMOTIONAL_PRICE) {
            // Validar precio promocional
            if (promotionalPrice == null || promotionalPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Datos inválidos. Verifique la oferta ingresada.");
            }
            if (promotionalPrice.compareTo(originalPrice) >= 0) {
                throw new RuntimeException("El precio promocional debe ser menor al precio original");
            }
        }
    }

    // Calcular precio final
    private BigDecimal calculateFinalPrice(BigDecimal originalPrice, OfferType type, 
                                          BigDecimal discountValue, BigDecimal promotionalPrice) {
        if (type == OfferType.PERCENTAGE_DISCOUNT && discountValue != null) {
            BigDecimal discount = originalPrice.multiply(discountValue)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            return originalPrice.subtract(discount);
        } else if (type == OfferType.PROMOTIONAL_PRICE && promotionalPrice != null) {
            return promotionalPrice;
        }
        return originalPrice;
    }

    // Determinar estado de la oferta
    private String determineOfferStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        
        if (today.isBefore(startDate)) {
            return "PRÓXIMA";
        } else if (today.isAfter(endDate)) {
            return "EXPIRADA";
        } else {
            return "VIGENTE";
        }
    }

    // Mapeo a OfferResponse
    private OfferResponse mapToOfferResponse(Offer offer) {
        BigDecimal finalPrice = calculateFinalPrice(
                offer.getProduct().getPrice(),
                offer.getType(),
                offer.getDiscountValue(),
                offer.getPromotionalPrice()
        );

        return OfferResponse.builder()
                .id(offer.getId())
                .productId(offer.getProduct().getId())
                .productName(offer.getProduct().getName())
                .originalPrice(offer.getProduct().getPrice())
                .type(offer.getType())
                .discountValue(offer.getDiscountValue())
                .promotionalPrice(offer.getPromotionalPrice())
                .finalPrice(finalPrice)
                .startDate(offer.getStartDate())
                .endDate(offer.getEndDate())
                .isActive(offer.getIsActive())
                .status(determineOfferStatus(offer.getStartDate(), offer.getEndDate()))
                .createdAt(offer.getCreatedAt())
                .createdBy(offer.getCreationUser() != null ? 
                          offer.getCreationUser().getFirstName() + " " + offer.getCreationUser().getLastName() : 
                          "Sistema")
                .build();
    }
}
