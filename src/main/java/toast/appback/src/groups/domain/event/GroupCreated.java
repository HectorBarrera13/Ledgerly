package toast.appback.src.groups.domain.event;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.shared.domain.DomainEvent;

public record GroupCreated(
        GroupId groupId,
        GroupInformation groupInformation
) implements DomainEvent {
}
