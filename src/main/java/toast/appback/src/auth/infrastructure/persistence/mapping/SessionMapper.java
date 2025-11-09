package toast.appback.src.auth.infrastructure.persistence.mapping;

import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.SessionStatus;
import toast.model.entities.account.SessionEntity;
import toast.model.entities.account.SessionStatusE;

public class SessionMapper {
    public static SessionEntity toEntity(Session session, SessionEntity existingEntity) {
        SessionEntity sessionEntity = existingEntity != null ? existingEntity : new SessionEntity();
        sessionEntity.setSessionId(session.getSessionId().value());
        sessionEntity.setSessionStatus(toEntitySession(session.getStatus()));
        sessionEntity.setExpiration(session.getExpiration());
        return sessionEntity;
    }

    public static Session toDomain(SessionEntity sessionEntity) {
        return Session.load(
                SessionId.load(sessionEntity.getSessionId()),
                sessionEntity.getExpiration(),
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
