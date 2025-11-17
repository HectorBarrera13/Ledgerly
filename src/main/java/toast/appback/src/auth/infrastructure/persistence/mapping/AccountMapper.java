package toast.appback.src.auth.infrastructure.persistence.mapping;

import toast.appback.src.auth.domain.*;
import toast.appback.src.users.domain.UserId;
import toast.model.entities.account.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountMapper {

    public static Account toDomain(AccountEntity entity) {
        List<Session> sessions = entity.getSessions().stream()
                .map(AccountMapper::toDomainSession)
                .collect(Collectors.toCollection(ArrayList::new));

        return new Account(
                AccountId.load(entity.getAccountId()),
                UserId.load(entity.getUser().getUserId()),
                Email.load(entity.getEmail()),
                Password.fromHashed(entity.getPasswordHash()),
                entity.getCreatedAt(),
                sessions
        );
    }

    public static void syncSessions(List<Session> domainSessions, AccountEntity entity) {
        List<SessionEntity> entitySessions = entity.getSessions();
        if (entitySessions == null) {
            entitySessions = new ArrayList<>();
            entity.setSessions(entitySessions);
        }

        // Índice rápido por UUID para las existentes
        var existingMap = entitySessions.stream()
                .collect(Collectors.toMap(SessionEntity::getSessionId, s -> s));

        // Agregar o actualizar sesiones
        for (Session domainSession : domainSessions) {
            SessionEntity existing = existingMap.get(domainSession.getSessionId().getValue());
            if (existing != null) {
                // Ya existe, actualizar campos
                existing.setSessionStatus(SessionStatusE.valueOf(domainSession.getStatus().name()));
                existing.setExpiration(domainSession.getExpiration());
            } else {
                // Nueva sesión → crear entidad
                SessionEntity newEntity = new SessionEntity();
                newEntity.setSessionId(domainSession.getSessionId().getValue());
                newEntity.setAccount(entity);
                newEntity.setExpiration(domainSession.getExpiration());
                newEntity.setSessionStatus(SessionStatusE.valueOf(domainSession.getStatus().name()));
                entitySessions.add(newEntity);
            }
        }

        // Eliminar las que ya no existen en el dominio
        entitySessions.removeIf(e ->
                domainSessions.stream().noneMatch(d -> d.getSessionId().getValue().equals(e.getSessionId()))
        );
    }

    private static Session toDomainSession(SessionEntity entity) {
        return Session.load(
                SessionId.load(entity.getSessionId()),
                SessionStatus.valueOf(entity.getSessionStatus().name()),
                entity.getExpiration()
        );
    }

}
