package toast.appback.src.debts.application.exceptions;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

/**
 * Excepción lanzada cuando la creación de una deuda falla por reglas del dominio.
 */
public class CreationDebtException extends DomainException {

    public CreationDebtException(List<DomainError> errors) {
        super(errors);
    }
}
