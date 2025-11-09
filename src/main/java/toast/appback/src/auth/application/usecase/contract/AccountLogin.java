package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.AccountAuthCommand;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.shared.UseCase;
import toast.appback.src.shared.errors.AppError;

public interface AccountLogin extends UseCase<TokenInfo, AppError, AccountAuthCommand> {
}
