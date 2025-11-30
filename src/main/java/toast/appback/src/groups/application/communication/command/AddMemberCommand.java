package toast.appback.src.groups.application.communication.command;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

public record AddMemberCommand(
        GroupId groupId,
        UserId actorId,
        List<UserId> membersId
) {
}
