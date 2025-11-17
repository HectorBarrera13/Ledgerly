package toast.appback.src.auth.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import toast.appback.src.auth.domain.event.AccountCreated;
import toast.appback.src.auth.infrastructure.persistence.jparepository.JpaAccountRepository;
import toast.model.entities.account.AccountEntity;

@Component
@RequiredArgsConstructor
public class AccountCreatedListener {
    private final JpaAccountRepository jpaAccountRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AccountCreated event) {
        AccountEntity accountEntity = jpaAccountRepository.findByAccountId(event.accountId().getValue())
                .orElseThrow();
        accountEntity.setLastUpdatedAt(accountEntity.getCreatedAt());
        jpaAccountRepository.save(accountEntity);
    }
}
