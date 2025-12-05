package toast.appback.src.debts.application.exceptions;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

/**
 * Excepción lanzada cuando la aceptación de una deuda falla por errores de dominio.
 */
public class AcceptDebtException extends DomainException {
    public AcceptDebtException(List<DomainError> errors) {
        super(errors);
    }
}
