package toast.appback.src.groups.application.communication.command;

import toast.appback.src.groups.domain.vo.GroupMember;
import toast.appback.src.users.domain.UserId;

import java.util.List;

public record CreateGroupCommand(
        String name,
        String description,
        UserId creatorId
) {
}
