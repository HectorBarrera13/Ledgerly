package toast.appback.src.debts.infrastructure.api.advisor;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toast.appback.src.debts.application.exceptions.CreditorNotFound;
import toast.appback.src.debts.application.exceptions.DebtNotFound;
import toast.appback.src.debts.application.exceptions.DebtorNotFound;
import toast.appback.src.debts.application.exceptions.UnauthorizedActionException;
import toast.appback.src.middleware.ErrorData;

@Order(1)
@RestControllerAdvice
public class DebtAdvisor {

    @ExceptionHandler(CreditorNotFound.class)
    public ResponseEntity<ErrorData> handleCreditorNotFoundException(CreditorNotFound ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorData);
    }

    @ExceptionHandler(DebtNotFound.class)
    public ResponseEntity<ErrorData> handleDebtNotFoundException(DebtNotFound ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorData);
    }

    @ExceptionHandler(DebtorNotFound.class)
    public ResponseEntity<ErrorData> handleDebtorNotFoundException(DebtorNotFound ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorData);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorData> handleUnauthorizedActionException(UnauthorizedActionException ex) {
        System.out.println(ex.getMessage());
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorData);
    }

}
