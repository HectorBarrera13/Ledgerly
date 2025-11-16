package toast.appback.src.auth.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.application.port.AccountReadRepository;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.infrastructure.persistence.jparepository.JpaAccountRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountReadRepositoryMySQL implements AccountReadRepository {
    private final JpaAccountRepository jpaAccountRepository;

    @Override
    public Optional<AccountView> findById(AccountId id) {
        return jpaAccountRepository.findProjectedByAccountId(id.getValue())
                .map(projection ->
                        new AccountView(
                                projection.getAccountId(),
                                projection.getEmail()
                        )
                );
    }
}
