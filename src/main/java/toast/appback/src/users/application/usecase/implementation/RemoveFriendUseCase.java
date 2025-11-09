package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.EventBus;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.shared.types.Result;
import toast.appback.src.users.application.communication.command.RemoveFriendCommand;
import toast.appback.src.users.application.usecase.contract.RemoveFriend;
import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.event.FriendRemoved;
import toast.appback.src.users.domain.repository.FriendShipRepository;

import java.util.Optional;

public class RemoveFriendUseCase implements RemoveFriend {
    private final FriendShipRepository friendShipRepository;
    private final EventBus eventBus;

    public RemoveFriendUseCase(FriendShipRepository friendShipRepository, EventBus eventBus) {
        this.friendShipRepository = friendShipRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Result<Void, AppError> execute(RemoveFriendCommand command) {
        Optional<FriendShip> foundUser = friendShipRepository.findByUsersIds(
                UserId.load(command.requesterId()),
                UserId.load(command.friendId())
        );
        foundUser.ifPresent(friendShip -> {
            friendShipRepository.delete(friendShip.getFriendshipId());
            eventBus.publish(new FriendRemoved(
                    friendShip.getFriendshipId(),
                    friendShip.getRequest().getId(),
                    friendShip.getReceiver().getId())
            );
        });
        return Result.success();
    }
}
