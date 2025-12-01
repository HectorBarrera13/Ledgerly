package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.application.ApplicationEventBus;
import toast.appback.src.users.application.communication.command.RemoveFriendCommand;
import toast.appback.src.users.application.exceptions.FriendShipNotFound;
import toast.appback.src.users.application.exceptions.RemoveMySelfFromFriendsException;
import toast.appback.src.users.application.usecase.contract.RemoveFriend;
import toast.appback.src.users.application.event.FriendShipBroke;
import toast.appback.src.users.domain.repository.FriendShipRepository;

public class  RemoveFriendUseCase implements RemoveFriend {
    private final FriendShipRepository friendShipRepository;
    private final ApplicationEventBus applicationEventBus;

    public RemoveFriendUseCase(FriendShipRepository friendShipRepository,
                               ApplicationEventBus applicationEventBus) {
        this.friendShipRepository = friendShipRepository;
        this.applicationEventBus = applicationEventBus;
    }

    @Override
    public void execute(RemoveFriendCommand command) {
        if (command.requesterId().equals(command.friendId())) {
            throw new RemoveMySelfFromFriendsException();
        }

        boolean existsFriendShip = friendShipRepository.existsFriendShip(
                command.requesterId(),
                command.friendId()
        );

        if (!existsFriendShip) {
            throw new FriendShipNotFound(command.requesterId(), command.friendId());
        }

        friendShipRepository.delete(command.requesterId(), command.friendId());

        applicationEventBus.publish(
                new FriendShipBroke(command.requesterId(), command.friendId())
        );
    }
}
