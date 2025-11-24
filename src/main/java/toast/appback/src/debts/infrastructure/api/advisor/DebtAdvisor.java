package toast.appback.src.debts.infrastructure.api.advisor;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toast.appback.src.debts.application.exceptions.*;
import toast.appback.src.middleware.ErrorData;

@Order(1)
@RestControllerAdvice
public class DebtAdvisor {

    @ExceptionHandler(AcceptDebtException.class)
    public ResponseEntity<ErrorData> handleAcceptDebtException(AcceptDebtException ex) {
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorData);
    }

    @ExceptionHandler(CreationDebtException.class)
    public ResponseEntity<ErrorData> handleCreationDebtException(CreationDebtException ex) {
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorData);
    }

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

    @ExceptionHandler(EditDebtException.class)
    public ResponseEntity<ErrorData> handleEditDebtException(EditDebtException ex) {
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorData);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorData> handleUnauthorizedActionException(UnauthorizedActionException ex) {
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorData);
    }

}
