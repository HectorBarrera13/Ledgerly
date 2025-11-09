package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.UseCase;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.users.domain.User;

public interface CreateUser extends UseCase<User, AppError, CreateUserCommand> {
}
