package toast.appback.src.auth.domain.event;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.Email;
import toast.appback.src.shared.DomainEvent;
import toast.appback.src.users.domain.UserId;

public record AccountCreated(
        AccountId accountId,
        UserId userId,
        Email email
) implements DomainEvent {
}