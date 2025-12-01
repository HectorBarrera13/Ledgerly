package toast.appback.src.auth.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.auth.domain.*;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.auth.infrastructure.persistence.jparepository.JpaAccountRepository;
import toast.appback.src.auth.infrastructure.persistence.mapping.AccountMapper;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;
import toast.model.entities.account.AccountEntity;
import toast.model.entities.users.UserEntity;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryMySQL implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;

    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<Account> findByEmail(String email) {
        return jpaAccountRepository.findByEmail(email)
                .map(AccountMapper::toDomain);
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return jpaAccountRepository.findByAccountId(accountId.getValue())
                .map(AccountMapper::toDomain);
    }

    @Override
    public void save(Account account) {
        UserEntity userEntity = jpaUserRepository.findByUserId(account.getUserId().getValue())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        AccountEntity accountEntity = jpaAccountRepository.findByEmail(account.getEmail().getValue())
                .orElse(new AccountEntity());
        accountEntity.setAccountId(account.getAccountId().getValue());
        accountEntity.setEmail(account.getEmail().getValue());
        accountEntity.setPasswordHash(account.getPassword().getHashed());
        accountEntity.setUser(userEntity);
        accountEntity.setCreatedAt(account.getCreatedAt());
        AccountMapper.syncSessions(account.getSessions(), accountEntity);
        jpaAccountRepository.save(accountEntity);
    }
}
