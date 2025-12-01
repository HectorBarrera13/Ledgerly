package toast.appback.src.auth.application.communication.command;

import toast.appback.src.users.domain.UserId;

public record CreateAccountCommand(
        UserId userId,
        String email,
        String password
) {
}
