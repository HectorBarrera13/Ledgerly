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

import java.util.Optional;
import java.util.UUID;

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
        if (command.requesterId().equals(command.receiverId())) {
            throw new FriendToMySelfException();
        }

        Optional<User> requester = userRepository.findById(command.requesterId());
        if (requester.isEmpty()) {
            throw new RequesterNotFound(command.requesterId());
        }

        Optional<User> receiver = userRepository.findById(command.receiverId());
        if (receiver.isEmpty()) {
            throw new ReceiverNotFound(command.receiverId());
        }

        Optional<FriendShip> existingFriendship = friendShipRepository.findByUsersIds(
                command.requesterId(),
                command.receiverId()
        );
        System.out.println(existingFriendship);

        if (existingFriendship.isPresent()) {
            throw new ExistingFriendShipException(command.requesterId(), command.receiverId());
        }

        User first = command.requesterId().getValue()
                .compareTo(command.receiverId().getValue()) < 0 ?
                requester.get() : receiver.get();

        User second = command.receiverId().getValue()
                .compareTo(command.requesterId().getValue()) < 0 ?
                receiver.get() : requester.get();

        FriendShip newfriendShip = FriendShip.create(first, second);

        friendShipRepository.save(newfriendShip);

        User receiverUser = receiver.get();
        eventBus.publishAll(newfriendShip.pullEvents());
        return new FriendView(
                receiverUser.getUserId().getValue(),
                receiverUser.getName().getFirstName(),
                receiverUser.getName().getLastName(),
                receiverUser.getPhone().getValue(),
                newfriendShip.getAddTime()
        );
    }
}
