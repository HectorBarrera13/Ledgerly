package toast.appback.src.users.application.communication.command;

import java.util.UUID;

public record CreateUserCommand(
        UUID userId,
        String firstName,
        String lastName,
        Phone phone
) {
    public record Phone(
            String countryCode,
            String number
    ) {
    }
}