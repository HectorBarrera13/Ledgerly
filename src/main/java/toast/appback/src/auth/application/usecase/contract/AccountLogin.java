package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.AccountAuthCommand;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.shared.application.UseCaseFunction;

public interface AccountLogin extends UseCaseFunction<TokenInfo, AccountAuthCommand> {
}
