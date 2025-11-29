package toast.appback.src.groups.application.usecase.contract;

import toast.appback.src.groups.application.communication.command.CreateGroupCommand;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.shared.application.UseCaseFunction;

public interface CreateGroup extends UseCaseFunction<Group, CreateGroupCommand> {
}
