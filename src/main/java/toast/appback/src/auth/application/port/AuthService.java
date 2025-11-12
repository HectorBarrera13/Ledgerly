package toast.appback.src.auth.application.port;

import toast.appback.src.auth.application.communication.command.AccountAuthCommand;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.application.AppError;

public interface AuthService {
    Result<Void, AppError> authenticate(AccountAuthCommand account);

    Result<Account, AppError> invalidateSession(AccountId accountId, SessionId sessionId);
}