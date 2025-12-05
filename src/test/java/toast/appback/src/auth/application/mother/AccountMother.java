package toast.appback.src.auth.application.mother;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Email;
import toast.appback.src.auth.domain.Password;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

public class AccountMother {

    private static final int MAX_SESSIONS = 10;

    public static Result<Account, DomainError> create(UserId userId, String email, String password) {
        Result<Email, DomainError> emailResult = Email.create(email);
        Result<Password, DomainError> passwordResult = Password.fromPlain(password, new FakePasswordHasher());
        Result<Void, DomainError> creationResult = Result.empty();
        creationResult.collect(emailResult);
        creationResult.collect(passwordResult);
        if (creationResult.isFailure()) {
            return creationResult.castFailure();
        }
        Email validEmail = emailResult.get();
        Password validPassword = passwordResult.get();
        Account account = Account.create(
                userId,
                validEmail,
                validPassword
        );
        return Result.ok(account);
    }

    public static Result<Account, DomainError> create(CreateAccountCommand command) {
        return create(
                command.userId(),
                command.email(),
                command.password()
        );
    }

    public static Account withEmail(String email) {
        return build(new CreateAccountCommand(
                UserId.generate(),
                email,
                "ValidPassword123"
        ));
    }

    public static Account withUserId(UserId userId) {
        return build(new CreateAccountCommand(
                userId,
                "valid@gmail.com",
                "ValidPassword123"
        ));
    }

    public static Account withMaxSessions() {
        Account account = validAccount();
        for (int i = 1; i <= MAX_SESSIONS; i++) {
            account.startSession()
                    .orElseThrow(result -> new IllegalStateException(
                            "AccountMother failed to create max sessions\n" + result
                    ));
        }
        return account;
    }

    public static Account withMaxSessions(String email) {
        Account account = withEmail(email);
        for (int i = 1; i <= MAX_SESSIONS; i++) {
            account.startSession()
                    .orElseThrow(result -> new IllegalStateException(
                            "AccountMother failed to create max sessions\n" + result
                    ));
        }
        return account;
    }

    public static Account withPassword(String password) {
        return build(new CreateAccountCommand(
                UserId.generate(),
                "valid@gmail.com",
                password
        ));
    }

    public static Account validAccount() {
        return withEmail("valid@gmail.com");
    }

    public static Account invalidAccount() {
        return build(new CreateAccountCommand(
                UserId.generate(),
                "invalid-email",
                "123"
        ));
    }

    private static Account build(CreateAccountCommand command) {
        return create(command).orElseThrow(result ->
                new IllegalStateException(
                        "AccountMother failed to create account:\n" + result
                )
        );
    }

    public static class FakePasswordHasher implements PasswordHasher {
        @Override
        public String hash(String raw) {
            return "hashed_" + raw;
        }

        @Override
        public boolean verify(String raw, String hashed) {
            return hashed.equals("hashed_" + raw);
        }
    }
}