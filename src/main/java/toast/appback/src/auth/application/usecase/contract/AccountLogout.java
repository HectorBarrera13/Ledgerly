package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.shared.application.UseCase;
import toast.appback.src.shared.application.AppError;

public interface AccountLogout extends UseCase<Void, AppError, String> {
}
