package toast.appback.src.debts.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

import java.util.UUID;

public class DebtorNotFound extends ApplicationException {
    private final UUID debtorId;

    public DebtorNotFound(UUID debtorId) {
        super("debtor with ID " + debtorId + " not found.");
        this.debtorId = debtorId;
    }

    public UUID getDebtorId() {
        return debtorId;
    }
}
