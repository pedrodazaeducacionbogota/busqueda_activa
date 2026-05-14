package co.gov.educacionbogota.sicobertura.busquedaactiva.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "co.gov.educacionbogota.sicobertura.busquedaactiva")
public class BusquedaActivaExceptionHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BusquedaActivaExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("BA RuntimeException", ex);
        Map<String, Object> response = new HashMap<>();
        response.put("codigo", "404");
        response.put("mensaje", ex.getMessage());
        response.put("data", null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        log.error("BA Exception", ex);
        Map<String, Object> response = new HashMap<>();
        response.put("codigo", "500");
        response.put("mensaje", "Error interno del servidor: " + ex.getMessage());
        response.put("data", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
