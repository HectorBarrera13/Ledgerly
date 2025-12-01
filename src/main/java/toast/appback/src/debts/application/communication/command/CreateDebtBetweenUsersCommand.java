package toast.appback.src.debts.application.communication.command;

import toast.appback.src.users.domain.UserId;

import java.util.UUID;

public record CreateDebtBetweenUsersCommand(
        String purpose,
        String description,
        String currency,
        Long amount,
        UserId debtorId,
        UserId creditorId
        ) {

}
