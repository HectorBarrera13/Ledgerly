package toast.appback.src.auth.application.exceptions;

import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.application.ApplicationException;

public class InvalidSessionException extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "session with id %s is invalid.";
    private static final String FRIENDLY_MESSAGE = "the session is invalid.";

    private final SessionId sessionId;
    public InvalidSessionException(SessionId sessionId) {
        super(String.format(MESSAGE_TEMPLATE, sessionId.getValue()), FRIENDLY_MESSAGE);
        this.sessionId = sessionId;
    }

    public SessionId getSessionId() {
        return sessionId;
    }
}
