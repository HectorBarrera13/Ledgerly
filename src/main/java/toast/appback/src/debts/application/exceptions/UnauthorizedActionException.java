package toast.appback.src.debts.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

/**
 * Excepción lanzada cuando un usuario intenta realizar una acción no autorizada sobre una deuda.
 */
public class UnauthorizedActionException extends ApplicationException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
