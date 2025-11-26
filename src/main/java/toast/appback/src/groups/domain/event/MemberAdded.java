package toast.appback.src.groups.domain.event;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.UserId;

public record MemberAdded(
        GroupId groupId,
        UserId userId
) implements DomainEvent {
}
