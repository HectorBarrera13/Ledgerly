package toast.appback.src.auth.application.communication;

import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.users.application.communication.command.CreateUserCommand;

public class AuthCommandMapper {
    public static CreateUserCommand accountCommandToCreateCommand(RegisterAccountCommand command) {
        return new CreateUserCommand(
                command.firstName(),
                command.lastName(),
                command.phoneCountryCode(),
                command.phoneNumber()
        );
    }
}
