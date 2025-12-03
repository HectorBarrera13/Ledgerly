package toast.appback.src.groups.application.communication.command;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

import java.util.List;

public record AddMemberCommand(
        GroupId groupId,
        UserId actorId,
        List<UserId> membersId
) {
}
