package toast.appback.src.debts.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

import java.util.UUID;

/**
 * Excepción lanzada cuando no se encuentra una deuda por su identificador.
 */
public class DebtNotFound extends ApplicationException {
    private final UUID debtId;

    public DebtNotFound(UUID debtId) {
        super("debt with ID " + debtId + " not found.");
        this.debtId = debtId;
    }

    public UUID getDebtId() {
        return debtId;
    }
}
