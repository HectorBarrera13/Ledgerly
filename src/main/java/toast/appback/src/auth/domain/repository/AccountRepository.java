package toast.appback.src.auth.domain.repository;

import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountId;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByEmail(String email);
    Optional<Account> findById(AccountId accountId);
    void save(Account account);
    void updateSessions(Account account);
}
