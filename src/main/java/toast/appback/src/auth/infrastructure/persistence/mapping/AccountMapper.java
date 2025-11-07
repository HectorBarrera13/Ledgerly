package toast.appback.src.auth.infrastructure.persistence.mapping;

import toast.appback.src.auth.domain.*;
import toast.appback.src.shared.DomainEvent;
import toast.appback.src.users.domain.UserId;
import toast.model.entities.account.AccountEntity;
import toast.model.entities.account.SessionStatusE;
import toast.model.entities.account.TokenEntity;

import java.time.Instant;
import java.util.List;

public class AccountMapper {
    public static AccountEntity toEntity(Account account, AccountEntity existingEntity) {
        AccountEntity accountEntity = existingEntity != null ? existingEntity : new AccountEntity();
        accountEntity.setAccountId(account.getAccountId().id());
        accountEntity.setEmail(account.getEmail().getValue());
        accountEntity.setPasswordHash(account.getPassword().hashed());
        return accountEntity;
    }

    public static Account toDomain(AccountEntity accountEntity, List<TokenEntity> tokens, List<DomainEvent> domainEvents) {
        List<Session> sessions = accountEntity.getSessions().stream().map(
                s -> Session.load(
                        SessionId.create(s.getSessionId()),
                        "",
                        "",
                        Instant.now(),
                        toDomainSession(s.getSessionStatus())
                )
        ).toList();
        return new Account(
                AccountId.create(accountEntity.getAccountId()),
                UserId.create(accountEntity.getUser().getUserId()),
                Email.create(accountEntity.getEmail()).getValue(),
                Password.fromHashed(accountEntity.getPasswordHash()),
                sessions,
                domainEvents
        );
    }

    public static SessionStatusE toEntitySession(SessionStatus sessionStatus) {
        return switch (sessionStatus) {
            case NORMAL -> SessionStatusE.NORMAL;
            case REVOKED -> SessionStatusE.REVOKED;
        };
    }

    public static SessionStatus toDomainSession(SessionStatusE sessionStatusE) {
        return switch (sessionStatusE) {
            case NORMAL -> SessionStatus.NORMAL;
            case REVOKED -> SessionStatus.REVOKED;
        };
    }
}
