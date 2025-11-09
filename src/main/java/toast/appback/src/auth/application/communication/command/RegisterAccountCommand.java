package toast.appback.src.auth.application.communication.command;

public record RegisterAccountCommand(
        String firstName,
        String lastName,
        String email,
        String password,
        PhoneCommand phone
) {
}
