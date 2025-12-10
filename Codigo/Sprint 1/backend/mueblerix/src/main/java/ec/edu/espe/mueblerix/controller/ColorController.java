package ec.edu.espe.mueblerix.controller;

import ec.edu.espe.mueblerix.dto.request.CreateColorRequest;
import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import ec.edu.espe.mueblerix.dto.response.ColorResponse;
import ec.edu.espe.mueblerix.service.product.ColorService;
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
public class ColorController {

    private final ColorService colorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ColorResponse>>> getAllColors() {
        log.info("Fetching all colors");
        List<ColorResponse> colors = colorService.getAllColors();
        return ResponseEntity.ok(ApiResponse.success("Colores obtenidos exitosamente", colors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ColorResponse>> getColorById(@PathVariable Long id) {
        log.info("Fetching color with ID: {}", id);
        ColorResponse color = colorService.getColorById(id);
        return ResponseEntity.ok(ApiResponse.success("Color obtenido exitosamente", color));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ColorResponse>> createColor(
            @Valid @RequestBody CreateColorRequest request) {
        log.info("Creating color: {}", request.getName());
        ColorResponse color = colorService.createColor(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Color creado exitosamente", color));
    }
}
