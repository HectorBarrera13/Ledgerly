package toast.appback.src.auth.application.port;

import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.domain.AccountId;

import java.util.Optional;

public interface AccountReadRepository {
    Optional<AccountView> findById(AccountId id);
}
