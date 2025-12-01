package toast.appback.src.users.application.communication.command;

public record CreateUserCommand(
        String firstName,
        String lastName,
        String phoneCountryCode,
        String phoneNumber
) {
}