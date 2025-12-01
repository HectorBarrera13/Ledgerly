package toast.appback.src.debts.application.exceptions;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

public class AcceptDebtException extends DomainException {
    public AcceptDebtException(List<DomainError> errors) {
        super(errors);
    }
}
