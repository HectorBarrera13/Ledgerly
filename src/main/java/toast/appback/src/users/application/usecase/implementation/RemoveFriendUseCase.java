package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.shared.errors.DomainError;
import toast.appback.src.users.application.usecase.contract.RemoveFriend;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class RemoveFriendUseCase implements RemoveFriend {

    private final UserRepository userRepository;

    public RemoveFriendUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Result<Void, AppError> remove(UUID userId, UUID friendId) {
        Optional<User> maybeUser = userRepository.findById(userId);
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            Result<Void, DomainError> result = user.removeFriend(UserId.create(friendId));
            if (result.isFailure()) {
                return Result.failure(AppError.domainError(result.getErrors()));
            }
            userRepository.save(user);
            return Result.success();
        }
        return Result.failure(AppError.entityNotFound("User").withDetails("accountId: " + userId.toString()));
    }
}
