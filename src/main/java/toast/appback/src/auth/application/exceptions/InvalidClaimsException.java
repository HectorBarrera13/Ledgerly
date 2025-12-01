package toast.appback.src.auth.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

public class InvalidClaimsException extends ApplicationException {
    private static final String FRIENDLY_MESSAGE = "invalid token claims";

    public InvalidClaimsException(String message) {
        super(message, FRIENDLY_MESSAGE);
    }
}
