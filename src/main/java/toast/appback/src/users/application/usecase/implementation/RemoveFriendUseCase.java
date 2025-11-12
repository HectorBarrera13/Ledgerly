package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.application.EventBus;
import toast.appback.src.users.application.communication.command.RemoveFriendCommand;
import toast.appback.src.users.application.exceptions.FriendNotFound;
import toast.appback.src.users.application.usecase.contract.RemoveFriend;
import toast.appback.src.users.domain.FriendShip;
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
    public void execute(RemoveFriendCommand command) {
        Optional<FriendShip> foundUser = friendShipRepository.findByUsersIds(
                command.requesterId(),
                command.friendId()
        );

        if (foundUser.isEmpty()) {
            throw new FriendNotFound(command.friendId());
        }

        FriendShip friendShip = foundUser.get();

        friendShipRepository.delete(friendShip.getFriendshipId());

        eventBus.publish(
                new FriendRemoved(
                        friendShip.getFriendshipId(),
                        friendShip.getRequest().getUserId(),
                        friendShip.getReceiver().getUserId())
        );
    }
}
