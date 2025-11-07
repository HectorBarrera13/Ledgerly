package toast.appback.src.auth.domain.repository;

import toast.appback.src.auth.domain.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Optional<Account> findByEmail(String email);
    Optional<Account> findBySessionId(UUID sessionId);
    Optional<Account> findByAccountIdAndSessionId(UUID userId, UUID sessionId);
    Account save(Account account);
    void updateSessions(Account account);
    void deactivate(Account account);
}
