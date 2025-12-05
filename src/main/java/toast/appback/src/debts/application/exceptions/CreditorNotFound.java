package toast.appback.src.debts.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

import java.util.UUID;

/**
 * Excepción lanzada cuando no se encuentra el usuario acreedor requerido por una operación.
 */
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
