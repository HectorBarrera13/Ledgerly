package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.application.UseCaseFunction;
import toast.appback.src.users.application.communication.command.AddFriendCommand;
import toast.appback.src.users.application.communication.result.FriendView;

public interface AddFriend extends UseCaseFunction<FriendView, AddFriendCommand> {
}
