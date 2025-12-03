package toast.appback.src.debts.application.communication.result;

import toast.model.entities.CursorIdentifiable;

import java.math.BigDecimal;
import java.util.UUID;

public record DebtBetweenUsersView(
        UUID debtId,
        String purpose,
        String description,
        BigDecimal amount,
        String currency,
        String status,
        UserSummaryView debtorSummary,
        UserSummaryView creditorSummary
) implements DebtView, CursorIdentifiable<UUID> {
    @Override
    public UUID getCursorId() {
        return this.debtId;
    }
}
