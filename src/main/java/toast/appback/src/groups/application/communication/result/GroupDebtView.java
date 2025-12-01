package toast.appback.src.groups.application.communication.result;

import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.model.entities.CursorIdentifiable;

import java.math.BigDecimal;
import java.util.UUID;

public record GroupDebtView(
        UUID DebtId,
        String purpose,
        String description,
        BigDecimal amount,
        String currency,
        String status,
        UserSummaryView debtorSummary,
        UserSummaryView creditorSummary
) implements CursorIdentifiable<UUID> {
    @Override
    public UUID getCursorId() {
        return this.DebtId;
    }
}
