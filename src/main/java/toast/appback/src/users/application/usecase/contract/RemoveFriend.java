package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.application.UseCase;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.users.application.communication.command.RemoveFriendCommand;

public interface RemoveFriend extends UseCase<Void, AppError, RemoveFriendCommand> {
}
