package toast.appback.src.auth.infrastructure.persistence.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import toast.model.entities.account.AccountEntity;

import java.util.Optional;
import java.util.UUID;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByEmail(String email);

    Optional<AccountEntity> findByAccountId(UUID accountId);
}
