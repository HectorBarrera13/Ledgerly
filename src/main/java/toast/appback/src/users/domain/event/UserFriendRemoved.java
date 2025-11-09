package toast.appback.src.users.domain.event;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.UserId;

public record UserFriendRemoved(
        UserId userId,
        UserId friendId
) implements DomainEvent {
}
