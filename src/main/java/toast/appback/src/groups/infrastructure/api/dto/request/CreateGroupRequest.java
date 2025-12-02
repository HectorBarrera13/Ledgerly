package toast.appback.src.groups.infrastructure.api.dto.request;

import toast.appback.src.groups.application.communication.command.AddMemberCommand;
import toast.appback.src.groups.application.communication.command.CreateGroupCommand;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record CreateGroupRequest(
        String name,
        String description,
        List<UUID> members
) {
    public CreateGroupCommand toCreateGroupCommand(UserId creatorId) {
        return new CreateGroupCommand(
            name,
            description,
            creatorId
        );
    }

    public AddMemberCommand toAddMemberCommand(GroupId groupId, UserId actorId) {
        List<UUID> finalMembers = new ArrayList<>(members);
        if (!finalMembers.contains(actorId.getValue())) {
            finalMembers.add(actorId.getValue());
        }

        return new AddMemberCommand(
            groupId,
            actorId,
            finalMembers.stream().map(UserId::load).toList()
        );
    }
}
