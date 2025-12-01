package toast.appback.src.auth.domain.event;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.domain.DomainEvent;

public record SessionRevoked(
        AccountId accountId,
        SessionId sessionId
) implements DomainEvent {
}
