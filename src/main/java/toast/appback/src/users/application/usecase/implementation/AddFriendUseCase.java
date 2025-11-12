package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.middleware.ErrorsHandler;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.users.application.communication.command.AddFriendCommand;
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
    public void execute(AddFriendCommand command) {
        Optional<User> requester = userRepository.findById(UserId.load(command.requesterId()));
        if (requester.isEmpty()) {
            ErrorsHandler.handleError(AppError.entityNotFound("Requester user", "not found"));
        }

        Optional<User> receiver = userRepository.findById(UserId.load(command.receiverId()));
        if (receiver.isEmpty()) {
            ErrorsHandler.handleError(AppError.entityNotFound("Receiver user", "not found"));
        }

        User requestUser = requester.get();
        User receiverUser = receiver.get();

        FriendShip newfriendShip = FriendShip.create(requestUser, receiverUser);

        friendShipRepository.save(newfriendShip);

        eventBus.publishAll(newfriendShip.pullEvents());
    }
}
