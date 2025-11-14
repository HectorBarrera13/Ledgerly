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
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.FriendShipRepository;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;

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

        Optional<User> firstUser = userRepository.findById(command.firstUserId());
        if (firstUser.isEmpty()) {
            throw new RequesterNotFound(command.firstUserId());
        }

        Optional<User> secondUser = userRepository.findById(command.secondUserId());
        if (secondUser.isEmpty()) {
            throw new ReceiverNotFound(command.secondUserId());
        }

        Optional<FriendShip> existingFriendship = friendShipRepository.findByUsersIds(
                command.firstUserId(),
                command.secondUserId()
        );

        if (existingFriendship.isPresent()) {
            throw new ExistingFriendShipException(command.firstUserId(), command.secondUserId());
        }

        UserId first = firstUser.get().getUserId();
        UserId second = secondUser.get().getUserId();

        FriendShip newfriendShip = FriendShip.create(first, second);

        friendShipRepository.save(newfriendShip);

        User targetUser = secondUser.get();

        eventBus.publishAll(newfriendShip.pullEvents());

        return new FriendView(
                targetUser.getUserId().getValue(),
                targetUser.getName().getFirstName(),
                targetUser.getName().getLastName(),
                targetUser.getPhone().getValue(),
                newfriendShip.getCreatedAt()
        );
    }
}
