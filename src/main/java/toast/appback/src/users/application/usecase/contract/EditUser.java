package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.application.UseCaseFunction;
import toast.appback.src.users.application.communication.command.EditUserCommand;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.domain.User;

public interface EditUser extends UseCaseFunction<UserView, EditUserCommand> {
}
