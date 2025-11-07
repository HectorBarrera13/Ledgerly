package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.auth.application.communication.result.RegisterAccountResult;

public interface RegisterAccount {
    Result<RegisterAccountResult, AppError> register(RegisterAccountCommand command);
}
