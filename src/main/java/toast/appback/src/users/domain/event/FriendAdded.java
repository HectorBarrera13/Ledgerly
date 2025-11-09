package toast.appback.src.users.domain.event;

import toast.appback.src.shared.DomainEvent;
import toast.appback.src.users.domain.UserId;

public record FriendAdded(UserId userId, UserId friendId) implements DomainEvent {
}
