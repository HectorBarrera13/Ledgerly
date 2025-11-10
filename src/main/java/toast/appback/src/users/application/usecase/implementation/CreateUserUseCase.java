package toast.appback.src.users.application.usecase.implementation;

import toast.appback.src.middleware.ErrorsHandler;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.application.usecase.contract.CreateUser;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserFactory;
import toast.appback.src.users.domain.repository.UserRepository;

public class CreateUserUseCase implements CreateUser {

    private final UserRepository userRepository;

    private final UserFactory userFactory;

    private final EventBus eventBus;

    public CreateUserUseCase(UserRepository userRepository,
                             UserFactory userFactory,
                             EventBus eventBus) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
        this.eventBus = eventBus;
    }

    @Override
    public User execute(CreateUserCommand command) {
        Result<User, DomainError> newUser = userFactory.create(
                        command.firstName(),
                        command.lastName(),
                        command.phone().countryCode(),
                        command.phone().number()
                );
        System.out.println(newUser.getErrors());
        newUser.ifFailure(ErrorsHandler::handleErrors);

        userRepository.save(newUser.getValue());

        User user = newUser.getValue();

        eventBus.publishAll(user.pullEvents());

        return user;
    }
}