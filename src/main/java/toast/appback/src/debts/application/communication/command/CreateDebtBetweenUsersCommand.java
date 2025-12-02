package toast.appback.src.debts.application.communication.command;

import toast.appback.src.users.domain.UserId;

public record CreateDebtBetweenUsersCommand(
        String purpose,
        String description,
        String currency,
        Long amount,
        UserId debtorId,
        UserId creditorId
        ) {

}
