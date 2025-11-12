package toast.appback.src.middleware;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ExceptionAdvisor {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorData> handleAllExceptions(Exception ex) {
        // Log the exception or perform other actions as needed
        System.err.println("An unexpected error occurred: " + ex.getMessage());
        ErrorData errorData = new ErrorData(null, "An unexpected error occurred. Please try again later.", null, Instant.now(), 500);
        return ResponseEntity.status(500).body(errorData);
    }
}
