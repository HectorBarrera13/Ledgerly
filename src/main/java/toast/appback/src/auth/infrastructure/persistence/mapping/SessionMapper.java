package toast.appback.src.auth.infrastructure.persistence.mapping;

import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.SessionStatus;
import toast.appback.src.auth.domain.Token;
import toast.model.entities.account.SessionEntity;
import toast.model.entities.account.SessionStatusE;

public class SessionMapper {
    public static SessionEntity toEntity(Session session, SessionEntity existingEntity) {
        SessionEntity sessionEntity = existingEntity != null ? existingEntity : new SessionEntity();
        sessionEntity.setSessionId(session.sessionId().id());
        sessionEntity.setSessionStatus(toEntitySession(session.status()));
        return sessionEntity;
    }

    public static Session toDomain(SessionEntity sessionEntity, Token token) {
        return Session.load(
                SessionId.create(sessionEntity.getSessionId()),
                token.value(),
                token.tokenType(),
                token.expiresAt(),
                toDomainSession(sessionEntity.getSessionStatus())
        );
    }

    private static SessionStatusE toEntitySession(SessionStatus sessionStatus) {
        return switch (sessionStatus) {
            case NORMAL -> SessionStatusE.NORMAL;
            case REVOKED -> SessionStatusE.REVOKED;
        };
    }

    private static SessionStatus toDomainSession(SessionStatusE sessionStatusE) {
        return switch (sessionStatusE) {
            case NORMAL -> SessionStatus.NORMAL;
            case REVOKED -> SessionStatus.REVOKED;
        };
    }
}
