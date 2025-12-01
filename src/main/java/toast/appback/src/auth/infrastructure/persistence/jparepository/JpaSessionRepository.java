package toast.appback.src.auth.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import toast.model.entities.account.SessionEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaSessionRepository extends JpaRepository<SessionEntity, Long> {
    Optional<SessionEntity> findBySessionId(UUID sessionId);

    @Modifying
    @Query("""
    DELETE FROM SessionEntity s
    WHERE s.expiration < CURRENT_TIMESTAMP OR s.sessionStatus = 'REVOKED'
    """)
    boolean cleanExpiredSessions();
}
