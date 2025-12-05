package toast.appback.src.middleware;

import org.springframework.http.HttpStatus;
import toast.appback.src.shared.domain.DomainErrType;
import toast.appback.src.shared.errors.ErrorTypeV;
import toast.appback.src.shared.errors.IError;

import java.util.ArrayList;
import java.util.List;

/**
 * Manejador de errores para convertir errores de dominio en respuestas HTTP adecuadas.
 */
public class ErrorsHandler {
    private ErrorsHandler() {
    }

    /**
     * Maneja un solo error y lo convierte en un objeto ErrorData.
     *
     * @param error El error a manejar.
     * @return Objeto ErrorData representando el error.
     */
    public static ErrorData handleSingleError(IError error) {
        return ErrorData.create(
                error.field(),
                error.message(),
                mapErrorToStatusCode(error.type())
        );
    }

    /**
     * Maneja múltiples errores y los convierte en un objeto ErrorData.
     *
     * @param errors Lista de errores a manejar.
     * @return Objeto ErrorData representando los errores.
     */
    public static ErrorData handleMultipleErrors(List<? extends IError> errors) {
        List<ErrorDetails> details = new ArrayList<>();
        errors.forEach(err ->
                details.add(new ErrorDetails(err.field(), err.message()))
        );
        return ErrorData.create(
                "Multiple errors occurred",
                details
        );
    }

    /**
     * Maneja un error desconocido y lo convierte en un objeto ErrorData genérico.
     *
     * @return Objeto ErrorData representando un error desconocido.
     */
    public static ErrorData unknownError() {
        return ErrorData.create("An unexpected error occurred. Please try again later.");
    }

    /**
     * Mapea un tipo de error a un código de estado HTTP correspondiente.
     *
     * @param errorType El tipo de error a mapear.
     * @return Código de estado HTTP correspondiente.
     */
    private static HttpStatus mapErrorToStatusCode(ErrorTypeV errorType) {
        if (errorType instanceof DomainErrType domainErrType) {
            return mapDomainErrorToStatusCode(domainErrType);
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Mapea un tipo de error de dominio a un código de estado HTTP correspondiente.
     *
     * @param type El tipo de error de dominio a mapear.
     * @return Código de estado HTTP correspondiente.
     */
    private static HttpStatus mapDomainErrorToStatusCode(DomainErrType type) {
        return switch (type) {
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case BUSINESS_RULE_VIOLATION -> HttpStatus.FORBIDDEN;
            case UNEXPECTED_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
