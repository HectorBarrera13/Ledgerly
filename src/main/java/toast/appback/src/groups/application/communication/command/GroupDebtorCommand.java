package toast.appback.src.groups.application.communication.command;

import toast.appback.src.users.domain.UserId;

public record GroupDebtorCommand(
        UserId debtorId,
        Long amount
) {
}
