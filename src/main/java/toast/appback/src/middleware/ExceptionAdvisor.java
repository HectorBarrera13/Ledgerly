package toast.appback.src.middleware;

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

    @ExceptionHandler(DomainException.class)
    @Order(1)
    public ResponseEntity<ErrorData> handleDomainException(DomainException ex) {
        List<DomainError> errors = ex.getErrors();
        if (errors.isEmpty()) {
            ErrorData errorData = ErrorsHandler.unknownError();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorData);
        } else if (errors.size() == 1) {
            ErrorData errorData = ErrorsHandler.handleSingleError(errors.getFirst());
            return ResponseEntity.status(errorData.status()).body(errorData);
        } else {
            ErrorData errorData = ErrorsHandler.handleMultipleErrors(errors);
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(errorData);
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
