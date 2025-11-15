package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.application.EventBus;
import toast.appback.src.users.application.communication.command.AddFriendCommand;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.application.exceptions.ExistingFriendShipException;
import toast.appback.src.users.application.exceptions.FriendToMySelfException;
import toast.appback.src.users.application.exceptions.ReceiverNotFound;
import toast.appback.src.users.application.exceptions.RequesterNotFound;
import toast.appback.src.users.application.usecase.contract.AddFriend;
import toast.appback.src.users.domain.FriendShip;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.FriendShipRepository;
import toast.appback.src.users.domain.repository.UserRepository;

public class AddFriendUseCase implements AddFriend {
    private final FriendShipRepository friendShipRepository;
    private final UserRepository userRepository;
    private final EventBus eventBus;

    public AddFriendUseCase(FriendShipRepository friendShipRepository, UserRepository userRepository, EventBus eventBus) {
        this.friendShipRepository = friendShipRepository;
        this.userRepository = userRepository;
        this.eventBus = eventBus;
    }

    @Override
    public FriendView execute(AddFriendCommand command) {
        if (command.firstUserId().equals(command.secondUserId())) {
            throw new FriendToMySelfException();
        }

        User first = userRepository.findById(command.firstUserId())
                .orElseThrow(() -> new RequesterNotFound(command.firstUserId()));

        User second = userRepository.findById(command.secondUserId())
                .orElseThrow(() -> new ReceiverNotFound(command.secondUserId()));

        boolean existsFriendShip = friendShipRepository.existsFriendShip(
                command.firstUserId(),
                command.secondUserId()
        );

        if (existsFriendShip) {
            throw new ExistingFriendShipException(command.firstUserId(), command.secondUserId());
        }

        FriendShip newfriendShip = FriendShip.create(first.getUserId(), second.getUserId());

        friendShipRepository.save(newfriendShip);

        eventBus.publishAll(newfriendShip.pullEvents());

        return new FriendView(
                second.getUserId().getValue(),
                second.getName().getFirstName(),
                second.getName().getLastName(),
                second.getPhone().getValue(),
                newfriendShip.getCreatedAt()
        );
    }
}
