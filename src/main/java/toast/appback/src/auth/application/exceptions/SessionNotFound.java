package toast.appback.src.auth.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

import java.util.UUID;

public class SessionNotFound extends ApplicationException {
    private final UUID sessionId;
    private final UUID accountId;

    public SessionNotFound(UUID sessionId, UUID accountId) {
        super("session with ID " + sessionId + " for account with ID " + accountId + " not found.");
        this.sessionId = sessionId;
        this.accountId = accountId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public UUID getAccountId() {
        return accountId;
    }
}
