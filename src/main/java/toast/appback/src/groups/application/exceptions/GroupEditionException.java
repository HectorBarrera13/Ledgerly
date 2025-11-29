package toast.appback.src.groups.application.exceptions;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

public class GroupEditionException extends DomainException {
    public GroupEditionException(List<DomainError> errors) {
        super(errors);
    }
}
