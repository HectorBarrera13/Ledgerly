package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.shared.UseCase;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.auth.application.communication.result.RegisterAccountResult;

public interface RegisterAccount extends UseCase<RegisterAccountResult, AppError, RegisterAccountCommand> {
}
