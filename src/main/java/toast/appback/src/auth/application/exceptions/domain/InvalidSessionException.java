package toast.appback.src.auth.application.exceptions.domain;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

public class InvalidSessionException extends DomainException {
    public InvalidSessionException(List<DomainError> errors) {
        super(errors);
    }
}
