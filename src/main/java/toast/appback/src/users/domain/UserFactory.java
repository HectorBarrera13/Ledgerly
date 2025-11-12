package toast.appback.src.users.domain;

import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.application.communication.command.CreateUserCommand;

public abstract class UserFactory {
    public abstract Result<User, DomainError> create(CreateUserCommand command);
}