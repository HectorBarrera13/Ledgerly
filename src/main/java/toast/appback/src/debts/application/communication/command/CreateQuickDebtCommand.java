package toast.appback.src.debts.application.communication.command;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

public record CreateQuickDebtCommand(
        String purpose,
        String description,
        String currency,
        Long amount,
        UserId userId,
        String role,
        String targetUserName
) {

}
