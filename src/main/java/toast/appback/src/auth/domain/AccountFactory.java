package toast.appback.src.auth.domain;

import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.DomainError;
import toast.appback.src.users.domain.UserId;

public abstract class AccountFactory {
    public abstract Result<Account, DomainError> create(UserId userId, String email, String password);
}
