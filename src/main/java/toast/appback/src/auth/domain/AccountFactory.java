package toast.appback.src.auth.domain;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.domain.UserId;

public abstract class AccountFactory {
    public abstract Result<Account, DomainError> create(CreateAccountCommand command);
}
