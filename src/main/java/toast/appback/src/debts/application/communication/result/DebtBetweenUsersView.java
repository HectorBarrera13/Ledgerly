package toast.appback.src.debts.application.communication.result;

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
) implements DebtView {
}
