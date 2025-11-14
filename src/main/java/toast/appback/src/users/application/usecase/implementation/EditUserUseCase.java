package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.users.application.communication.command.EditUserCommand;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.application.exceptions.domain.UserEditionException;
import toast.appback.src.users.application.usecase.contract.EditUser;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.repository.UserRepository;

import java.util.Optional;

public class EditUserUseCase implements EditUser {
    private final UserRepository userRepository;

    public EditUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserView execute(EditUserCommand command) {
        Optional<User> foundUser = userRepository.findById(command.userId());
        if (foundUser.isEmpty()) {
            throw new UserNotFound(command.userId());
        }
        User user = foundUser.get();
        Result<Name, DomainError> editResult = Name.create(command.firstName(), command.lastName());
        editResult.ifFailureThrows(UserEditionException::new);
        Name newName = editResult.getValue();
        user.changeName(newName);
        userRepository.save(user);
        return new UserView(
                user.getUserId().getValue(),
                user.getName().getFirstName(),
                user.getName().getLastName(),
                user.getPhone().getValue()
        );
    }
}
