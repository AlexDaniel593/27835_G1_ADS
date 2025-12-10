package ec.edu.espe.mueblerix.service.product;

import ec.edu.espe.mueblerix.dto.request.CreateColorRequest;
import ec.edu.espe.mueblerix.dto.response.ColorResponse;
import ec.edu.espe.mueblerix.model.Color;
import ec.edu.espe.mueblerix.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ColorService {

    private final ColorRepository colorRepository;

    @Transactional(readOnly = true)
    public List<ColorResponse> getAllColors() {
        log.info("Fetching all active colors");
        List<Color> colors = colorRepository.findByIsActiveTrue();
        return colors.stream()
                .map(this::mapToColorResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ColorResponse getColorById(Long id) {
        log.info("Fetching color with ID: {}", id);
        Color color = colorRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Color no encontrado"));
        return mapToColorResponse(color);
    }

    @Transactional
    public ColorResponse createColor(CreateColorRequest request) {
        log.info("Creating new color: {}", request.getName());

        if (colorRepository.existsByName(request.getName())) {
            throw new RuntimeException("Ya existe un color con ese nombre");
        }

        Color color = Color.builder()
                .name(request.getName())
                .isActive(true)
                .build();

        Color savedColor = colorRepository.save(color);
        log.info("Color created successfully with ID: {}", savedColor.getId());
        return mapToColorResponse(savedColor);
    }

    private ColorResponse mapToColorResponse(Color color) {
        return ColorResponse.builder()
                .id(color.getId())
                .name(color.getName())
                .isActive(color.getIsActive())
                .build();
    }
}
