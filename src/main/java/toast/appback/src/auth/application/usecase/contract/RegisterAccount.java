package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.shared.application.UseCaseFunction;

public interface RegisterAccount extends UseCaseFunction<AuthResult, RegisterAccountCommand> {
}