package toast.appback.src.debts.application.communication.command;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

public record EditDebtCommand(
        UserId userId,
    DebtId debtId,
    String newPurpose,
    String newDescription,
    String newCurrency,
    Long newAmount
) {
}
