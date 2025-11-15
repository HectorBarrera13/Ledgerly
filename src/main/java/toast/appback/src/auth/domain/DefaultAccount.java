package toast.appback.src.auth.domain;
import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.domain.event.AccountCreated;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.users.domain.UserId;

public class DefaultAccount extends AccountFactory {

    private final PasswordHasher passwordHasher;

    public DefaultAccount(PasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    private Result<Account, DomainError> create(UserId userId, String email, String password) {
        Result<Email, DomainError> emailResult = Email.create(email);
        Result<Password, DomainError> passwordResult = Password.fromPlain(password, passwordHasher);
        Result<Void, DomainError> creationResult = Result.empty();
        creationResult.collect(emailResult);
        creationResult.collect(passwordResult);
        if (creationResult.isFailure()) {
            return creationResult.castFailure();
        }
        Email validEmail = emailResult.getValue();
        Password validPassword = passwordResult.getValue();
        Account account = new Account(
                AccountId.generate(),
                userId,
                validEmail,
                validPassword
        );
        account.recordEvent(
                new AccountCreated(
                        account.getAccountId(),
                        account.getUserId(),
                        account.getEmail()
                )
        );
        return Result.success(account);
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
