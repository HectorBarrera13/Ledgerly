package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;

public interface AccountLogout {
    Result<Void, AppError> logout(String authorizationHeader);
}
