package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.UseCase;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.users.application.communication.command.AddFriendCommand;

public interface AddFriend extends UseCase<Void, AppError, AddFriendCommand> {
}
