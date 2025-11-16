package toast.appback.src.auth.infrastructure.api.advisor;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toast.appback.src.auth.application.exceptions.*;
import toast.appback.src.auth.infrastructure.exceptions.AuthenticationServiceException;
import toast.appback.src.auth.infrastructure.exceptions.TokenClaimsException;
import toast.appback.src.auth.infrastructure.exceptions.TokenExpiredException;
import toast.appback.src.middleware.ErrorData;

@Order(1)
@RestControllerAdvice
public class AuthenticationAdvisor {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorData> handleTokenExpiredException(TokenExpiredException ex) {
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorData);
    }

    @ExceptionHandler(TokenClaimsException.class)
    public ResponseEntity<ErrorData> handleTokenClaimsException(TokenClaimsException ex) {
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorData);
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ErrorData> handleAuthenticationServiceException(AuthenticationServiceException ex) {
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorData);
    }

    @ExceptionHandler(InvalidClaimsException.class)
    public ResponseEntity<ErrorData> handleInvalidClaimsException(InvalidClaimsException ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorData);
    }

    @ExceptionHandler(AccountExistsException.class)
    public ResponseEntity<ErrorData> handleAccountExistsException(AccountExistsException ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorData);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorData> handleAccountNotFoundException(AccountNotFoundException ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorData);
    }
}
