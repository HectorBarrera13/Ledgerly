package toast.appback.src.auth.domain;
import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.domain.event.AccountCreated;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.domain.UserId;

public class DefaultAccount extends AccountFactory {

    private final PasswordHasher passwordHasher;

    public DefaultAccount(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    private Result<Account, DomainError> create(UserId userId, String email, String password) {
        if (userId == null) {
            return Result.failure(DomainError.validation("user id", "user id cannot be null"));
        }
        Result<Account, DomainError> result = Email.create(email)
                .flatMap(email_ -> Password.fromPlain(password, passwordHasher)
                        .map(password_ ->
                                new Account(
                                        AccountId.generate(),
                                        userId,
                                        email_,
                                        password_
                                )));
        result.ifSuccess(
                account -> account.recordEvent(
                        new AccountCreated(
                                account.getAccountId(),
                                account.getUserId(),
                                account.getEmail()
                        )
                )
        );
        return result;
    }

    @Override
    public Result<Account, DomainError> create(CreateAccountCommand command) {
        return create(
                command.userId(),
                command.email(),
                command.password()
        );
    }
}
