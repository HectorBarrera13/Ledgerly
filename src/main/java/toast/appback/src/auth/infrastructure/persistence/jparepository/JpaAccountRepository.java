package toast.appback.src.auth.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import toast.appback.src.auth.infrastructure.persistence.jparepository.projection.AccountProjection;
import toast.model.entities.account.AccountEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByEmail(String email);

    Optional<AccountEntity> findByAccountId(UUID accountId);

    @Query("SELECT a.accountId AS accountId, a.email AS email " +
           "FROM AccountEntity a " +
           "WHERE a.accountId = :accountId")
    Optional<AccountProjection> findProjectedByAccountId(UUID accountId);

    AccountEntity getReferenceByAccountId(UUID accountId);
}
