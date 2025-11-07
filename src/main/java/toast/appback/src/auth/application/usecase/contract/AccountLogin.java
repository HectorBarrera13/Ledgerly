package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.AccountAuthCommand;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;

public interface AccountLogin {
    Result<TokenInfo, AppError> login(AccountAuthCommand accountAuthCommand);
}
