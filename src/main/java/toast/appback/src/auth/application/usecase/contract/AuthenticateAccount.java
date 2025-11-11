package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.shared.application.UseCaseFunction;

public interface AuthenticateAccount extends UseCaseFunction<AccessToken, AuthenticateAccountCommand> {
}
