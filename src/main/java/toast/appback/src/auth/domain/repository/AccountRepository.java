package toast.appback.src.auth.domain.repository;

import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByEmail(String email);
    Optional<Account> findBySessionId(SessionId sessionId);
    Optional<Account> findById(AccountId accountId);
    Optional<Account> findByAccountIdAndSessionId(AccountId accountId, SessionId sessionId);
    void save(Account account);
    void updateSessions(Account account);
}
