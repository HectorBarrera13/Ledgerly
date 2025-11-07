package toast.appback.src.middleware;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvisor {

    @ExceptionHandler(ErrorHandler.class)
    public ResponseEntity<ErrorData> handleCustomException(ErrorHandler ex) {
        System.err.println("Handled custom exception: " + ex.getMessage() + " - " + ex.getErrorData().details());
        return new ResponseEntity<>(ex.getErrorData(), HttpStatusCode.valueOf(ex.getErrorData().status()));
    }
}
