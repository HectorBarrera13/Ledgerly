package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.application.UseCase;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.users.application.communication.command.AddFriendCommand;

public interface AddFriend extends UseCase<Void, AppError, AddFriendCommand> {
}
