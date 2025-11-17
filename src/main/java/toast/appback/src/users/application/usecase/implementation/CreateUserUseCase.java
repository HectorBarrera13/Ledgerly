package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.application.exceptions.domain.CreationUserException;
import toast.appback.src.users.application.usecase.contract.CreateUser;
import toast.appback.src.users.domain.*;
import toast.appback.src.users.domain.repository.UserRepository;

public class CreateUserUseCase implements CreateUser {
    private final UserRepository userRepository;

    public CreateUserUseCase(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(CreateUserCommand command) {
        Result<Name, DomainError> nameResult = Name.create(command.firstName(), command.lastName());
        Result<Phone, DomainError> phoneResult = Phone.create(command.phoneCountryCode(), command.phoneNumber());
        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(nameResult);
        emptyResult.collect(phoneResult);

        emptyResult.ifFailureThrows(CreationUserException::new);

        Name name = nameResult.get();
        Phone phone = phoneResult.get();
        User user = User.create(
                name,
                phone
        );

        userRepository.save(user);

        return user;
    }
}