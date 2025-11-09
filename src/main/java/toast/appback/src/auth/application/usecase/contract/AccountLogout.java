package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.shared.UseCase;
import toast.appback.src.shared.errors.AppError;

public interface AccountLogout extends UseCase<Void, AppError, String> {
}
