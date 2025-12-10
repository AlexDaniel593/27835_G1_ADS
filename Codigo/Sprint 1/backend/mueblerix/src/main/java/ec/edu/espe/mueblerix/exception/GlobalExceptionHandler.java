package ec.edu.espe.mueblerix.exception;

import ec.edu.espe.mueblerix.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
          MethodArgumentNotValidException ex) {
    log.error("=== VALIDATION ERROR OCCURRED ===");
    log.error("Exception message: {}", ex.getMessage());
    
    StringBuilder errorMessages = new StringBuilder();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errorMessages.append(fieldName).append(": ").append(errorMessage).append("; ");
      log.error("Field '{}': {}", fieldName, errorMessage);
    });

    return ResponseEntity
            .badRequest()
            .body(ApiResponse.error("Error de validación", errorMessages.toString()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
          IllegalArgumentException ex) {
    log.error("IllegalArgumentException: {}", ex.getMessage());
    return ResponseEntity
            .badRequest()
            .body(ApiResponse.error("Error en la solicitud", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred", ex);
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Error interno del servidor", "Ocurrió un error inesperado"));
  }
}
