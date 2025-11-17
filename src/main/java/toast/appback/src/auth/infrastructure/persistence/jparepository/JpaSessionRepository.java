package toast.appback.src.auth.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import toast.model.entities.account.SessionEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaSessionRepository extends JpaRepository<SessionEntity, Long> {
    Optional<SessionEntity> findBySessionId(UUID sessionId);
}
