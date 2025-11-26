package toast.appback.src.groups.application.communication.command;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

public record AddMemberCommand(
        UserId userId,
        GroupId groupId
) {
}
