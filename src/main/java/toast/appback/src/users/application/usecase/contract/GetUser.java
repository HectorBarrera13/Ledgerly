package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.users.domain.User;

import java.util.UUID;

public interface GetUser {
    Result<User, AppError> get(UUID userId);
}
