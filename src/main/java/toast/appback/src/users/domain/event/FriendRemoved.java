package toast.appback.src.users.domain.event;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.FriendShipId;
import toast.appback.src.users.domain.UserId;

public record FriendRemoved(
        FriendShipId friendshipId,
        UserId userId,
        UserId friendId
) implements DomainEvent {
}
