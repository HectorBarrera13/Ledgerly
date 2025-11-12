package toast.appback.src.auth.application.exceptions;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

public class SessionStartException extends DomainException {
    public SessionStartException(List<DomainError> errors) {
        super(errors);
    }
}
