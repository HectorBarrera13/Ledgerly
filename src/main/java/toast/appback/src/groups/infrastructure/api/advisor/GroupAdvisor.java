package toast.appback.src.groups.infrastructure.api.advisor;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toast.appback.src.groups.application.exceptions.CreationGroupException;
import toast.appback.src.groups.application.exceptions.GroupEditionException;
import toast.appback.src.groups.application.exceptions.GroupNotFound;
import toast.appback.src.middleware.ErrorData;

@Order(1)
@RestControllerAdvice
public class GroupAdvisor {

    @ExceptionHandler(CreationGroupException.class)
    public ResponseEntity<ErrorData> handleCreationGroupException(CreationGroupException ex) {
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorData);
    }

    @ExceptionHandler(GroupEditionException.class)
    public ResponseEntity<ErrorData> handleGroupEditionException(GroupEditionException ex) {
        String friendlyMessage = ex.getMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorData);
    }

    @ExceptionHandler(GroupNotFound.class)
    public ResponseEntity<ErrorData> handleGroupNotFoundException(GroupNotFound ex) {
        String friendlyMessage = ex.getFriendlyMessage();
        ErrorData errorData = ErrorData.create(friendlyMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorData);
    }
}
