package toast.appback.src.auth.application.exceptions;

import java.util.UUID;

public class InactiveSessionException extends RuntimeException {
    private final UUID accountId;

    public InactiveSessionException(UUID accountId) {
        super("session for account with ID " + accountId + " is inactive.");
        this.accountId = accountId;
    }

    public UUID getAccountId() {
        return accountId;
    }
}
