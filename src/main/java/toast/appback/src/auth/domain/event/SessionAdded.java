package toast.appback.src.auth.domain.event;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.DomainEvent;

public record SessionAdded(
        AccountId accountId,
        SessionId sessionId
) implements DomainEvent {
}
