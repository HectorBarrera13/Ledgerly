package toast.appback.src.auth.application.port;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;

public interface AuthService {
    Result<Void, AppError> authenticate(String email, String password);

    Result<Void, AppError> invalidateSession(AccountId accountId, SessionId sessionId);
}
