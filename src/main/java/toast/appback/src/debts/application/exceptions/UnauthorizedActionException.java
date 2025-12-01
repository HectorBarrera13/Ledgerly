package toast.appback.src.debts.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

public class UnauthorizedActionException extends ApplicationException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
