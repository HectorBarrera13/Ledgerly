package toast.appback.src.debts.application.communication.command;

import toast.appback.src.debts.domain.DebtId;

public record EditDebtCommand(
    DebtId debtId,
    String purpose,
    String description,
    String currency,
    Long amount
) {
}
