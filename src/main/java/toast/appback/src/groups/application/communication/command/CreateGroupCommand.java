package toast.appback.src.groups.application.communication.command;

import toast.appback.src.groups.domain.vo.GroupMember;

import java.util.List;

public record CreateGroupCommand(
        String name,
        String description,
        List<GroupMember> members
) {
}
