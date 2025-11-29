package toast.appback.src.groups.application.usecase.contract;

import toast.appback.src.groups.application.communication.command.EditGroupCommand;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.shared.application.UseCaseFunction;

public interface EditGroup extends UseCaseFunction<GroupView, EditGroupCommand> {
}
