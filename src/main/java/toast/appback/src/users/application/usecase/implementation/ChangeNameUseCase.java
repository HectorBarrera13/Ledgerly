package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.shared.errors.DomainError;
import toast.appback.src.users.application.usecase.contract.ChangeName;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class ChangeNameUseCase implements ChangeName {

    private final UserRepository userRepository;

    public ChangeNameUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Result<User, AppError> change(UUID userId, String firstName, String lastName) {
        Optional<User> maybeUser = userRepository.findById(userId);
        if (maybeUser.isEmpty()) {
            return Result.failure(AppError.entityNotFound("User not found.").withDetails("userId: " + userId));
        }
        User user = maybeUser.get();
        Result<Name, DomainError> newName = Name.create(firstName, lastName);
        if (newName.isFailure()) {
            return Result.failure(AppError.domainError(newName.getErrors()));
        }
        user.changeName(newName.getValue());
        User updatedUser = userRepository.save(user);
        return Result.success(updatedUser);
    }
}
