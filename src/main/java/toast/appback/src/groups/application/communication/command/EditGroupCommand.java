package toast.appback.src.groups.application.communication.command;

import toast.appback.src.groups.domain.vo.GroupId;

public record EditGroupCommand(
        GroupId groupId,
        String name,
        String description
) {
}
