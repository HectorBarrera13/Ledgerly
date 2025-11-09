package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.shared.application.UseCase;
import toast.appback.src.shared.application.AppError;

public interface RefreshSession extends UseCase<TokenInfo, AppError, String> {
}
