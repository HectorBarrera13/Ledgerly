package toast.appback.src.debts.application.communication.command;

import toast.appback.src.users.domain.UserId;

public record GetDebtsCommand(
        UserId actorId
) {
}
