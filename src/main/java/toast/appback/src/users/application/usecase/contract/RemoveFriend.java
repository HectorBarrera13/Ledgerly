package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;

import java.util.UUID;

public interface RemoveFriend {
    Result<Void, AppError> remove(UUID userId, UUID friendId);
}
