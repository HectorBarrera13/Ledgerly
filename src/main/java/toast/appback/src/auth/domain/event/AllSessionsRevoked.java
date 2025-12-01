package toast.appback.src.auth.domain.event;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.shared.domain.DomainEvent;

public record AllSessionsRevoked(
        AccountId accountId
) implements DomainEvent {
}
