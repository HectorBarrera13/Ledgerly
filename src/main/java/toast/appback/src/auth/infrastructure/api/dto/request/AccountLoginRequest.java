package toast.appback.src.auth.infrastructure.api.dto.request;

import toast.appback.src.auth.application.communication.command.AccountAuthCommand;

public record AccountLoginRequest(
    String email,
    String password
) {
    public AccountAuthCommand toCommand() {
        return new AccountAuthCommand(email, password);
    }
}
