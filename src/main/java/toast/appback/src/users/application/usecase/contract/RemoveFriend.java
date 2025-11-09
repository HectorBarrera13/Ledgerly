package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.UseCase;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.users.application.communication.command.RemoveFriendCommand;

public interface RemoveFriend extends UseCase<Void, AppError, RemoveFriendCommand> {
}
