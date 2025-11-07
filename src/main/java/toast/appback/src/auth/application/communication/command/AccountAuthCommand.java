package toast.appback.src.auth.application.communication.command;

public record AccountAuthCommand(
    String email,
    String password
) {
}
