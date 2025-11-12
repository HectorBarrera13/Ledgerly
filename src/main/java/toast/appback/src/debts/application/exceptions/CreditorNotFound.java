package toast.appback.src.debts.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

import java.util.UUID;

public class CreditorNotFound extends ApplicationException {
    private final UUID creditorId;

    public CreditorNotFound(UUID creditorId) {
        super("Creditor with ID " + creditorId + " not found.");
        this.creditorId = creditorId;
    }

    public UUID getCreditorId() {
        return creditorId;
    }
}
