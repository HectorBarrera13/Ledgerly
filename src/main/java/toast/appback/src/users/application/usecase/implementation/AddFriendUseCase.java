package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.users.application.usecase.contract.AddFriend;
import toast.appback.src.users.domain.Friend;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class AddFriendUseCase implements AddFriend {

    private final UserRepository userRepository;

    public AddFriendUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Result<User, AppError> add(UUID userId, UUID friendId) {
        Optional<User> maybeUser = userRepository.findById(userId);
        Optional<User> maybeOtherUser = userRepository.findById(friendId);
        if (maybeUser.isPresent() && maybeOtherUser.isPresent()) {
            User otherUser = maybeOtherUser.get();
            User user = maybeUser.get();
            UserId friendIdObj = UserId.create(otherUser.getId().uuid());
            Friend friendUser = Friend.create(friendIdObj);
            user.addFriend(friendUser);
            User updatedUser = userRepository.save(user);
            return Result.success(updatedUser);
        }
        Result<User, AppError> result = Result.success();
        if (maybeUser.isEmpty()) {
            result.flatMap(() -> Result.failure(AppError.entityNotFound("User not found.").withDetails("userId: " + userId)));
        }
        if (maybeOtherUser.isEmpty()) {
            result.flatMap(() -> Result.failure(AppError.entityNotFound("Friend user not found.").withDetails("friendId: " + friendId)));
        }
        return result;
    }
}
