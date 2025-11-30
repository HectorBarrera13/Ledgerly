package toast.appback.src.groups.application.usecase.contract;

import toast.appback.src.groups.application.communication.command.AddMemberCommand;
import toast.appback.src.shared.application.UseCaseFunction;
import toast.appback.src.users.application.communication.result.UserView;

import java.util.List;

public interface AddMember extends UseCaseFunction<List<UserView>, AddMemberCommand> {
}
