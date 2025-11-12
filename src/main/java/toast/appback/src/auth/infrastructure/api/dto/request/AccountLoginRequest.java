package toast.appback.src.auth.infrastructure.api.dto.request;

import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;

public record AccountLoginRequest(
    String email,
    String password
) {
    public AuthenticateAccountCommand toCommand() {
        return new AuthenticateAccountCommand(email, password);
    }
}
