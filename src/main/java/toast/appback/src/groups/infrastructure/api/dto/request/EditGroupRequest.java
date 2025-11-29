package toast.appback.src.groups.infrastructure.api.dto.request;

import toast.appback.src.groups.application.communication.command.EditGroupCommand;
import toast.appback.src.groups.domain.vo.GroupId;

public record EditGroupRequest(
        String name,
        String description
) {
    public EditGroupCommand toEditGroupCommand(GroupId groupId) {
        return new EditGroupCommand(
                groupId,
                name,
                description
        );
    }
}
