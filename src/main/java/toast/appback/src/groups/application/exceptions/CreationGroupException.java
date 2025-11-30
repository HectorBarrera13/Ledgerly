package toast.appback.src.groups.application.exceptions;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

public class CreationGroupException extends DomainException {
    public CreationGroupException(List<DomainError> errors) {
        super(errors);
    }
}
