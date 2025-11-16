package toast.appback.src.users.infrastructure.api.advisor;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toast.appback.src.middleware.ErrorData;
import toast.appback.src.users.application.exceptions.*;
import toast.appback.src.users.infrastructure.exceptions.FriendRequestException;

@Order(1)
@RestControllerAdvice
public class UserAdvisor {

    @ExceptionHandler(FriendRequestException.class)
    public ResponseEntity<ErrorData> handleFriendRequestException(FriendRequestException ex) {
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorData);
    }

    @ExceptionHandler(FriendShipNotFound.class)
    public ResponseEntity<ErrorData> handleFriendNotFoundException(FriendShipNotFound ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorData);
    }

    @ExceptionHandler(ReceiverNotFound.class)
    public ResponseEntity<ErrorData> handleReceiverNotFoundException(ReceiverNotFound ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorData);
    }

    @ExceptionHandler(RequesterNotFound.class)
    public ResponseEntity<ErrorData> handleRequesterNotFoundException(RequesterNotFound ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorData);
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ErrorData> handleUserNotFoundException(UserNotFound ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorData);
    }

    @ExceptionHandler(ExistingFriendShipException.class)
    public ResponseEntity<ErrorData> handleExistingFriendShipException(ExistingFriendShipException ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorData);
    }

    @ExceptionHandler(FriendToMySelfException.class)
    public ResponseEntity<ErrorData> handleFriendToMySelfException(FriendToMySelfException ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorData);
    }

    @ExceptionHandler(RemoveMySelfFromFriendsException.class)
    public ResponseEntity<ErrorData> handleRemoveMySelfFromFriendsException(RemoveMySelfFromFriendsException ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorData);
    }
}
