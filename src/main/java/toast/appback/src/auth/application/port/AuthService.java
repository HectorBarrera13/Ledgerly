package toast.appback.src.auth.application.port;

import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;

public interface AuthService {
    void authenticate(AuthenticateAccountCommand command);
}