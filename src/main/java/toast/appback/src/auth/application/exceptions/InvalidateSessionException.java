package toast.appback.src.auth.application.exceptions;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

public class InvalidateSessionException extends DomainException {

    public InvalidateSessionException(List<DomainError> errors) {
        super(errors);
    }
}
