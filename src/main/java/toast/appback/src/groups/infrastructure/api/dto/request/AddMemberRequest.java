package toast.appback.src.groups.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.groups.application.communication.command.AddMemberCommand;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

public record AddMemberRequest(
        @JsonProperty("new_member_ids")
        List<UUID> newMemberIds
) {
    public AddMemberCommand toAddMemberCommand(GroupId groupId, UserId actorId) {
        List<UserId> userIds = newMemberIds.stream()
                .map(UserId::load)
                .toList();

        return new AddMemberCommand(
                groupId,
                actorId,
                userIds
        );
    }
}
