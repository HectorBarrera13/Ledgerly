package toast.appback.src.users.application.event;

import toast.appback.src.shared.application.ApplicationEvent;
import toast.appback.src.users.domain.UserId;

public record FriendShipBroke(
        UserId userId,
        UserId friendId
) implements ApplicationEvent {
}
