package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;

public interface RefreshSession {
    Result<TokenInfo, AppError> refresh(String refreshToken);
}
