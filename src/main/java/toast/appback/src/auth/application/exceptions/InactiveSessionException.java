package toast.appback.src.auth.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

import java.util.UUID;

public class InactiveSessionException extends ApplicationException {
    private final UUID accountId;

    public InactiveSessionException(UUID accountId) {
        super("session for account with ID " + accountId + " is inactive.");
        this.accountId = accountId;
    }

    public UUID getAccountId() {
        return accountId;
    }
}
