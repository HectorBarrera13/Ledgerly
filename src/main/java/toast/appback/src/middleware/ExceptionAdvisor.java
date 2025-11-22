package toast.appback.src.middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

@RestControllerAdvice
public class ExceptionAdvisor {


    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvisor.class);

    @ExceptionHandler(DomainException.class)
    @Order(1)
    public ResponseEntity<ErrorData> handleDomainException(DomainException ex) {
        List<DomainError> errors = ex.getErrors();
        log.error("Domain errors: {}", errors);
        if (errors.isEmpty()) {
            ErrorData errorData = ErrorsHandler.unknownError();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorData);
        } else if (errors.size() == 1) {
            ErrorData errorData = ErrorsHandler.handleSingleError(errors.getFirst());
            return ResponseEntity.status(errorData.status()).body(errorData);
        } else {
            ErrorData errorData = ErrorsHandler.handleMultipleErrors(errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorData);
        }
    }

    @ExceptionHandler(Exception.class)
    @Order(10)
    public ResponseEntity<ErrorData> handleAllExceptions(Exception ex) {
        // Log the exception or perform other actions as needed
        System.err.println("An unexpected error occurred: " + ex.getMessage());
        ex.printStackTrace();
        ErrorData errorData = ErrorsHandler.unknownError();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorData);
    }
}
