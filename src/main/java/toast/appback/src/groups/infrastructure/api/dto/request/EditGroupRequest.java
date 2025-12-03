package toast.appback.src.groups.infrastructure.api.dto.request;

import toast.appback.src.groups.application.communication.command.EditGroupCommand;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

public record EditGroupRequest(
        String name,
        String description
) {
    public EditGroupCommand toEditGroupCommand(GroupId groupId, UserId creatorId) {
        return new EditGroupCommand(
                groupId,
                creatorId,
                name,
                description
        );
    }
}
