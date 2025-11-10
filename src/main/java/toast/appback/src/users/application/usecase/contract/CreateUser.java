package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.application.UseCaseFunction;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.domain.User;

public interface CreateUser extends UseCaseFunction<User, CreateUserCommand> {
}
