package toast.appback.src.users.application.exceptions.domain;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

public class UserEditionException extends DomainException {
    public UserEditionException(List<DomainError> errors) {
        super(errors);
    }
}
