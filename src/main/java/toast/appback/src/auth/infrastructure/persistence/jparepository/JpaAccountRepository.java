package toast.appback.src.auth.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import toast.model.entities.account.AccountEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByEmail(String email);

    @Query("SELECT a FROM AccountEntity a JOIN a.sessions s WHERE s.sessionId = :sessionId")
    Optional<AccountEntity> findBySessionId(UUID sessionId);

    @Query("SELECT a FROM AccountEntity a JOIN a.sessions s WHERE a.accountId = :accountId AND s.sessionId = :sessionId")
    Optional<AccountEntity> findByAccountIdAndSessionId(UUID accountId, UUID sessionId);

    Optional<AccountEntity> findByAccountId(UUID accountId);
}
