package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

public class CreationUserException extends DomainException {

    public CreationUserException(List<DomainError> errors) {
        super(errors);
    }
}
