package toast.appback.src.auth.application.communication.command;

public record AuthenticateAccountCommand(
        String email,
        String password
) {
}
