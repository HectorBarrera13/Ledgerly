package toast.appback.src.users.application.communication.command;

public record CreateUserCommand(
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