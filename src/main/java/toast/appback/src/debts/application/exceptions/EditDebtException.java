package toast.appback.src.debts.application.exceptions;

import toast.appback.src.shared.application.DomainException;
import toast.appback.src.shared.domain.DomainError;

import java.util.List;

/**
 * Excepción lanzada cuando la edición de una deuda falla por validaciones/errores de dominio.
 */
public class EditDebtException extends DomainException {

    public EditDebtException(List<DomainError> errors) {
        super(errors);
    }
}
