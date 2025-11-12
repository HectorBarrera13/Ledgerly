package toast.appback.src.users.domain.event;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.UserId;

public record UserCreated(
    UserId userId,
    Name name
) implements DomainEvent {
}
