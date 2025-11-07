package toast.appback.src.auth.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.auth.domain.*;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.auth.infrastructure.persistence.jparepository.JpaAccountRepository;
import toast.appback.src.auth.infrastructure.persistence.jparepository.TokenRepository;
import toast.appback.src.auth.infrastructure.persistence.mapping.AccountMapper;
import toast.appback.src.auth.infrastructure.persistence.mapping.SessionMapper;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;
import toast.model.entities.account.AccountEntity;
import toast.model.entities.account.SessionEntity;
import toast.model.entities.account.TokenEntity;
import toast.model.entities.users.UserEntity;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryMongo implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;

    private final JpaUserRepository jpaUserRepository;

    private final TokenRepository tokenRepository;

    @Override
    public Optional<Account> findByEmail(String email) {
        return jpaAccountRepository.findByEmail(email)
                .map(account -> {
                    List<TokenEntity> tokens = tokenRepository.findBySessionIds(
                            account.getSessions().stream()
                                    .map(SessionEntity::getSessionId)
                                    .toList()
                    );
                    return AccountMapper.toDomain(account, tokens, List.of());
                });
    }

    @Override
    public Optional<Account> findBySessionId(UUID sessionId) {
        return jpaAccountRepository.findBySessionId(sessionId)
                .map(account -> {
                    List<TokenEntity> tokens = tokenRepository.findBySessionIds(
                            account.getSessions().stream()
                                    .map(SessionEntity::getSessionId)
                                    .toList()
                    );
                    return AccountMapper.toDomain(account, tokens, List.of());
                });
    }

    @Override
    public Optional<Account> findByAccountIdAndSessionId(UUID userId, UUID sessionId) {
        return jpaAccountRepository.findByAccountIdAndSessionId(userId, sessionId)
                .map(account -> {
                    List<TokenEntity> tokens = tokenRepository.findBySessionIds(
                            account.getSessions().stream()
                                    .map(SessionEntity::getSessionId)
                                    .toList()
                    );
                    return AccountMapper.toDomain(account, tokens, List.of());
                });
    }

    @Override
    public Account save(Account account) {
        AccountEntity accountEntity = jpaAccountRepository.findByAccountId(account.getAccountId().id())
                .orElse(new AccountEntity());
        UserEntity userEntity = jpaUserRepository.findByUserId(account.getUserId().uuid())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + account.getUserId().uuid()));
        List<SessionEntity> sessionEntities = account.getSessions().stream()
                .map(s -> {
                    if (accountEntity.getSessions() == null) {
                        accountEntity.setSessions(new ArrayList<>());
                    }
                    SessionEntity existingSession = accountEntity.getSessions().stream()
                            .filter(es -> es.getSessionId().equals(s.sessionId().id()))
                            .findFirst()
                            .orElse(new SessionEntity());
                    SessionMapper.toEntity(s, existingSession);
                    return existingSession;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        accountEntity.setSessions(sessionEntities);
        sessionEntities.forEach(s -> s.setAccount(accountEntity));
        accountEntity.setUser(userEntity);
        AccountEntity savedAccount = jpaAccountRepository.save(AccountMapper.toEntity(account, accountEntity));
        return AccountMapper.toDomain(savedAccount, List.of(), account.pullDomainEvents());
    }

    @Override
    public void updateSessions(Account account) {
        AccountEntity accountEntity = jpaAccountRepository.findByAccountId(account.getAccountId().id())
                .orElseThrow(() -> new NoSuchElementException("Account not found with id: " + account.getAccountId().id()));
        List<SessionEntity> sessionEntities = account.getSessions().stream()
                .map(s -> {
                    if (accountEntity.getSessions() == null) {
                        accountEntity.setSessions(new ArrayList<>());
                    }
                    SessionEntity existingSession = accountEntity.getSessions().stream()
                            .filter(es -> es.getSessionId().equals(s.sessionId().id()))
                            .findFirst()
                            .orElse(new SessionEntity());
                    return SessionMapper.toEntity(s, existingSession);
                })
                .collect(Collectors.toCollection(ArrayList::new));
        accountEntity.getSessions().clear();
        accountEntity.getSessions().addAll(sessionEntities);
        sessionEntities.forEach(s -> s.setAccount(accountEntity));
        jpaAccountRepository.save(AccountMapper.toEntity(account, accountEntity));
    }

    @Override
    public void deactivate(Account account) {
        jpaAccountRepository.findByAccountId(account.getAccountId().id())
                .ifPresent(jpaAccountRepository::delete);
    }
}
