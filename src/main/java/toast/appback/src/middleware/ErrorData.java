package toast.appback.src.middleware;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/**
 * Registro que representa la estructura de datos de un error en la aplicación.
 *
 * @param field     campo en el que se produjo el error
 * @param message   mensaje descriptivo del error
 * @param details   detalles adicionales del error
 * @param timeStamp fecha y hora en que se produjo el error
 * @param status    código de estado HTTP asociado al error (no se incluye en la serialización JSON)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorData(
        String field,
        String message,
        List<ErrorDetails> details,
        @JsonProperty("time_stamp")
        Instant timeStamp,
        @JsonIgnore
        HttpStatus status
) {
    /**
     * Crea una instancia de ErrorData con el mensaje y los detalles proporcionados.
     *
     * @param message mensaje del error
     * @param details lista de detalles del error
     * @return nueva instancia de ErrorData
     */
    public static ErrorData create(String message, List<ErrorDetails> details) {
        return new ErrorData(null, message, details, Instant.now(), null);
    }

    /**
     * Crea una instancia de ErrorData con el mensaje proporcionado.
     *
     * @param message mensaje del error
     * @return nueva instancia de ErrorData
     */
    public static ErrorData create(String message) {
        return new ErrorData(null, message, null, Instant.now(), null);
    }

    /**
     * Crea una instancia de ErrorData con el campo, mensaje y estado HTTP proporcionados.
     *
     * @param fieldName nombre del campo donde ocurrió el error
     * @param message   mensaje del error
     * @param status    código de estado HTTP asociado al error
     * @return nueva instancia de ErrorData
     */
    public static ErrorData create(String fieldName, String message, HttpStatus status) {
        return new ErrorData(fieldName, message, null, Instant.now(), status);
    }
}
