package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.application.exceptions.domain.CreationUserException;
import toast.appback.src.users.application.usecase.contract.CreateUser;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserFactory;
import toast.appback.src.users.domain.repository.UserRepository;

public class  CreateUserUseCase implements CreateUser {
    private final UserRepository userRepository;
    private final UserFactory userFactory;

    public CreateUserUseCase(
            UserRepository userRepository,
            UserFactory userFactory
    ) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
    }

    @Override
    public User execute(CreateUserCommand command) {
        User newUser = userFactory.create(command)
                .orElseThrow(CreationUserException::new);

        userRepository.save(newUser);

        return newUser;
    }
}