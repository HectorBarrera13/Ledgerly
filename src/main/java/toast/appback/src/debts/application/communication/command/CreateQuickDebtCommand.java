package toast.appback.src.debts.application.communication.command;

import toast.appback.src.users.domain.UserId;

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
