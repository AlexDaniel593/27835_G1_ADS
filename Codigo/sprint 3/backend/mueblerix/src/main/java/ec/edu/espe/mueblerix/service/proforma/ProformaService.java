package ec.edu.espe.mueblerix.service.proforma;

import ec.edu.espe.mueblerix.dto.request.CreateProformaRequest;
import ec.edu.espe.mueblerix.dto.request.UpdateProformaRequest;
import ec.edu.espe.mueblerix.dto.response.ProformaResponse;
import ec.edu.espe.mueblerix.exception.ResourceNotFoundException;
import ec.edu.espe.mueblerix.model.*;
import ec.edu.espe.mueblerix.model.enums.ModificationType;
import ec.edu.espe.mueblerix.model.enums.OfferType;
import ec.edu.espe.mueblerix.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProformaService {

    private final ProformaRepository proformaRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    private static final BigDecimal IVA_PERCENTAGE = new BigDecimal("0.15"); // 15% IVA

    /**
     * REQ010: Generar proforma con aplicación automática de ofertas
     */
    @Transactional
    public ProformaResponse createProforma(CreateProformaRequest request, Long userId) {
        log.info("Creating proforma for customer: {}", request.getCustomer().getIdentification());

        // 1. Obtener o crear cliente
        Customer customer = getOrCreateCustomer(request.getCustomer());

        // 2. Obtener usuario creador
        User creationUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 3. Generar código único de proforma
        String proformaCode = generateProformaCode();

        // 4. Crear proforma (sin detalles primero)
        Proforma proforma = Proforma.builder()
                .code(proformaCode)
                .customer(customer)
                .creationUser(creationUser)
                .subtotal(BigDecimal.ZERO)
                .totalDiscount(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .isActive(true)
                .isDeleted(false)
                .emissionDate(LocalDateTime.now())
                .build();

        // 5. Procesar detalles de la proforma con ofertas
        BigDecimal subtotalBeforeDiscount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        Set<ProformaDetail> details = new HashSet<>();

        for (CreateProformaRequest.ProformaDetailRequest detailRequest : request.getDetails()) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + detailRequest.getProductId()));

            // Verificar que el producto esté activo
            if (!product.getIsActive() || product.getIsDeleted()) {
                throw new IllegalArgumentException("El producto " + product.getName() + " no está disponible");
            }

            // REQ010-3: Buscar oferta activa para el producto
            Offer activeOffer = findActiveOffer(product);

            // Calcular precios
            BigDecimal unitPrice = product.getPrice();
            BigDecimal unitDiscount = BigDecimal.ZERO;
            BigDecimal finalUnitPrice = unitPrice;

            if (activeOffer != null) {
                if (activeOffer.getType() == OfferType.PERCENTAGE_DISCOUNT) {
                    // Descuento porcentual
                    unitDiscount = unitPrice.multiply(activeOffer.getDiscountValue())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    finalUnitPrice = unitPrice.subtract(unitDiscount);
                } else if (activeOffer.getType() == OfferType.PROMOTIONAL_PRICE) {
                    // Precio promocional
                    finalUnitPrice = activeOffer.getPromotionalPrice();
                    unitDiscount = unitPrice.subtract(finalUnitPrice);
                }
            }

            BigDecimal detailSubtotal = finalUnitPrice.multiply(new BigDecimal(detailRequest.getQuantity()));
            BigDecimal detailDiscount = unitDiscount.multiply(new BigDecimal(detailRequest.getQuantity()));

            // Crear detalle SIN asignar la proforma aún
            ProformaDetail detail = ProformaDetail.builder()
                    .product(product)
                    .quantity(detailRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .unitDiscount(unitDiscount)
                    .subtotal(detailSubtotal)
                    .appliedOffer(activeOffer)
                    .build();

            details.add(detail);
            subtotalBeforeDiscount = subtotalBeforeDiscount.add(unitPrice.multiply(new BigDecimal(detailRequest.getQuantity())));
            totalDiscount = totalDiscount.add(detailDiscount);
        }

        // 6. Calcular totales
        BigDecimal subtotal = subtotalBeforeDiscount.subtract(totalDiscount);
        BigDecimal tax = subtotal.multiply(IVA_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        proforma.setSubtotal(subtotal);
        proforma.setTotalDiscount(totalDiscount);
        proforma.setTax(tax);
        proforma.setTotal(total);
        
        // Asignar la proforma a cada detalle y agregar a la colección
        for (ProformaDetail detail : details) {
            detail.setProforma(proforma);
        }
        proforma.setDetails(details);

        // 7. Guardar proforma
        Proforma savedProforma = proformaRepository.save(proforma);

        log.info("Proforma created successfully with code: {}", savedProforma.getCode());

        return mapToResponse(savedProforma);
    }

    /**
     * REQ010-3: Buscar oferta activa para un producto
     */
    private Offer findActiveOffer(Product product) {
        LocalDate today = LocalDate.now();
        return offerRepository.findByProductId(product.getId()).stream()
                .filter(Offer::getIsActive)
                .filter(offer -> !today.isBefore(offer.getStartDate()) && !today.isAfter(offer.getEndDate()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtener o crear cliente
     */
    private Customer getOrCreateCustomer(CreateProformaRequest.CustomerData customerData) {
        return customerRepository.findByIdentification(customerData.getIdentification())
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .identification(customerData.getIdentification())
                            .name(customerData.getName())
                            .address(customerData.getAddress())
                            .phone(customerData.getPhone())
                            .email(customerData.getEmail())
                            .proformas(new HashSet<>())
                            .build();
                    return customerRepository.save(newCustomer);
                });
    }

    /**
     * Generar código único de proforma (formato: PRO-YYYYMM-NNNN)
     */
    private String generateProformaCode() {
        LocalDateTime now = LocalDateTime.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        Long count = proformaRepository.countProformasThisMonth();
        Long nextNumber = (count != null ? count : 0L) + 1;
        String sequential = String.format("%04d", nextNumber);
        return String.format("PRO-%s-%s", yearMonth, sequential);
    }

    /**
     * Obtener todas las proformas activas
     */
    @Transactional(readOnly = true)
    public List<ProformaResponse> getAllProformas() {
        List<Proforma> proformas = proformaRepository.findAllActive();
        return proformas.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener proforma por ID
     */
    @Transactional(readOnly = true)
    public ProformaResponse getProformaById(Long id) {
        Proforma proforma = proformaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proforma no encontrada"));
        return mapToResponse(proforma);
    }

    /**
     * REQ012-1: Buscar proformas con filtros
     */
    @Transactional(readOnly = true)
    public List<ProformaResponse> searchProformas(
            String code, 
            String customerName, 
            String customerIdentification, 
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        
        log.info("Searching proformas with filters - code: {}, customerName: {}, identification: {}, startDate: {}, endDate: {}", 
                code, customerName, customerIdentification, startDate, endDate);
        
        List<Proforma> proformas = proformaRepository.findByFilters(
                code, customerName, customerIdentification, startDate, endDate);
        
        return proformas.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * REQ011: Actualizar proforma
     */
    @Transactional
    public ProformaResponse updateProforma(Long proformaId, UpdateProformaRequest request, Long userId) {
        log.info("Updating proforma ID: {} by user: {}", proformaId, userId);

        // 1. Obtener proforma existente
        Proforma proforma = proformaRepository.findById(proformaId)
                .orElseThrow(() -> new ResourceNotFoundException("Proforma no encontrada"));

        // Verificar que no esté eliminada
        if (proforma.getIsDeleted()) {
            throw new IllegalStateException("No se puede actualizar una proforma eliminada");
        }

        // 2. Obtener usuario que actualiza
        User updateUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 3. Actualizar datos del cliente
        Customer customer = proforma.getCustomer();
        customer.setName(request.getCustomer().getName());
        customer.setIdentification(request.getCustomer().getIdentification());
        customer.setAddress(request.getCustomer().getAddress());
        customer.setPhone(request.getCustomer().getPhone());
        customer.setEmail(request.getCustomer().getEmail());
        customerRepository.save(customer);

        // 4. Limpiar detalles existentes y procesar nuevos
        proforma.getDetails().clear();

        // 5. Procesar nuevos detalles con ofertas
        BigDecimal subtotalBeforeDiscount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (UpdateProformaRequest.ProformaDetailRequest detailRequest : request.getDetails()) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + detailRequest.getProductId()));

            if (!product.getIsActive() || product.getIsDeleted()) {
                throw new IllegalArgumentException("El producto " + product.getName() + " no está disponible");
            }

            // Buscar oferta activa
            Offer activeOffer = findActiveOffer(product);

            // Calcular precios
            BigDecimal unitPrice = product.getPrice();
            BigDecimal unitDiscount = BigDecimal.ZERO;
            BigDecimal finalUnitPrice = unitPrice;

            if (activeOffer != null) {
                if (activeOffer.getType() == OfferType.PERCENTAGE_DISCOUNT) {
                    unitDiscount = unitPrice.multiply(activeOffer.getDiscountValue())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    finalUnitPrice = unitPrice.subtract(unitDiscount);
                } else if (activeOffer.getType() == OfferType.PROMOTIONAL_PRICE) {
                    finalUnitPrice = activeOffer.getPromotionalPrice();
                    unitDiscount = unitPrice.subtract(finalUnitPrice);
                }
            }

            BigDecimal detailSubtotal = finalUnitPrice.multiply(new BigDecimal(detailRequest.getQuantity()));
            BigDecimal detailDiscount = unitDiscount.multiply(new BigDecimal(detailRequest.getQuantity()));

            // Crear detalle y agregarlo directamente a la colección existente
            ProformaDetail detail = ProformaDetail.builder()
                    .proforma(proforma)
                    .product(product)
                    .quantity(detailRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .unitDiscount(unitDiscount)
                    .subtotal(detailSubtotal)
                    .appliedOffer(activeOffer)
                    .build();

            proforma.getDetails().add(detail);
            subtotalBeforeDiscount = subtotalBeforeDiscount.add(unitPrice.multiply(new BigDecimal(detailRequest.getQuantity())));
            totalDiscount = totalDiscount.add(detailDiscount);
        }

        // 6. Recalcular totales
        BigDecimal subtotal = subtotalBeforeDiscount.subtract(totalDiscount);
        BigDecimal tax = subtotal.multiply(IVA_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        proforma.setSubtotal(subtotal);
        proforma.setTotalDiscount(totalDiscount);
        proforma.setTax(tax);
        proforma.setTotal(total);
        proforma.setUpdateUser(updateUser);
        proforma.setUpdatedAt(LocalDateTime.now());

        // 7. Registrar historial de modificación
        ProformaModificationHistory history = ProformaModificationHistory.builder()
                .proforma(proforma)
                .user(updateUser)
                .type(ModificationType.UPDATE)
                .description(request.getModificationReason() != null ? 
                        request.getModificationReason() : "Actualización de proforma")
                .build();
        proforma.getModificationHistory().add(history);

        // 8. Guardar proforma actualizada
        Proforma updatedProforma = proformaRepository.save(proforma);

        log.info("Proforma {} updated successfully", updatedProforma.getCode());

        return mapToResponse(updatedProforma);
    }

    /**
     * Mapear entidad a DTO
     */
    private ProformaResponse mapToResponse(Proforma proforma) {
        return ProformaResponse.builder()
                .id(proforma.getId())
                .code(proforma.getCode())
                .customer(ProformaResponse.CustomerInfo.builder()
                        .id(proforma.getCustomer().getId())
                        .name(proforma.getCustomer().getName())
                        .identification(proforma.getCustomer().getIdentification())
                        .address(proforma.getCustomer().getAddress())
                        .phone(proforma.getCustomer().getPhone())
                        .email(proforma.getCustomer().getEmail())
                        .build())
                .details(proforma.getDetails().stream()
                        .map(detail -> ProformaResponse.ProformaDetailInfo.builder()
                                .id(detail.getId())
                                .productId(detail.getProduct().getId())
                                .productName(detail.getProduct().getName())
                                .quantity(detail.getQuantity())
                                .unitPrice(detail.getUnitPrice())
                                .unitDiscount(detail.getUnitDiscount())
                                .subtotal(detail.getSubtotal())
                                .appliedOffer(detail.getAppliedOffer() != null ?
                                        ProformaResponse.OfferInfo.builder()
                                                .id(detail.getAppliedOffer().getId())
                                                .type(detail.getAppliedOffer().getType().toString())
                                                .discountValue(detail.getAppliedOffer().getDiscountValue())
                                                .promotionalPrice(detail.getAppliedOffer().getPromotionalPrice())
                                                .build()
                                        : null)
                                .build())
                        .collect(Collectors.toList()))
                .subtotal(proforma.getSubtotal())
                .totalDiscount(proforma.getTotalDiscount())
                .tax(proforma.getTax())
                .total(proforma.getTotal())
                .emissionDate(proforma.getEmissionDate())
                .createdByUser(proforma.getCreationUser().getFirstName() + " " + proforma.getCreationUser().getLastName())
                .build();
    }
}
