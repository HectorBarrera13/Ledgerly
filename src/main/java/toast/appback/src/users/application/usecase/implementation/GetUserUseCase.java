package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.users.application.usecase.contract.GetUser;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class GetUserUseCase implements GetUser {

    private final UserRepository userRepository;

    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Result<User, AppError> get(UUID userId) {
        Optional<User> maybeUser = userRepository.findById(userId);
        if (maybeUser.isEmpty()) {
            return Result.failure(AppError.entityNotFound("User")
                    .withDetails("User with ID " + userId + " not found."));
        }
        return Result.success(maybeUser.get());
    }
}
