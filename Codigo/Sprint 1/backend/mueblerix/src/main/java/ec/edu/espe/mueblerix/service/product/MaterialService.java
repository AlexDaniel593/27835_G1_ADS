package ec.edu.espe.mueblerix.service.product;

import ec.edu.espe.mueblerix.dto.request.CreateMaterialRequest;
import ec.edu.espe.mueblerix.dto.response.MaterialResponse;
import ec.edu.espe.mueblerix.model.Material;
import ec.edu.espe.mueblerix.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialService {

    private final MaterialRepository materialRepository;

    @Transactional(readOnly = true)
    public List<MaterialResponse> getAllMaterials() {
        log.info("Fetching all active materials");
        List<Material> materials = materialRepository.findByIsActiveTrue();
        return materials.stream()
                .map(this::mapToMaterialResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MaterialResponse getMaterialById(Long id) {
        log.info("Fetching material with ID: {}", id);
        Material material = materialRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado"));
        return mapToMaterialResponse(material);
    }

    @Transactional
    public MaterialResponse createMaterial(CreateMaterialRequest request) {
        log.info("Creating new material: {}", request.getName());

        if (materialRepository.existsByName(request.getName())) {
            throw new RuntimeException("Ya existe un material con ese nombre");
        }

        Material material = Material.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .build();

        Material savedMaterial = materialRepository.save(material);
        log.info("Material created successfully with ID: {}", savedMaterial.getId());
        return mapToMaterialResponse(savedMaterial);
    }

    private MaterialResponse mapToMaterialResponse(Material material) {
        return MaterialResponse.builder()
                .id(material.getId())
                .name(material.getName())
                .description(material.getDescription())
                .isActive(material.getIsActive())
                .build();
    }
}
