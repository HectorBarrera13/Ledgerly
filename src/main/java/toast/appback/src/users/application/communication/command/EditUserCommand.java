package toast.appback.src.users.application.communication.command;

import toast.appback.src.users.domain.UserId;

public record EditUserCommand(
        UserId userId,
        String firstName,
        String lastName
) {
}
