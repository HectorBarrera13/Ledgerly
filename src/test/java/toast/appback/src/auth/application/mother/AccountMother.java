package toast.appback.src.auth.application.mother;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.DefaultAccount;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.users.domain.UserId;

public class AccountMother {

    private static final int MAX_SESSIONS = 5;

    private static final DefaultAccount factory =
            new DefaultAccount(new FakePasswordHasher());

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
        Session session = account.startSession()
                 .orElseThrow(result -> new IllegalStateException(
                         "AccountMother failed to create max sessions\n" + result
                 ));
        long maxSessions = session.getMaxDurationSeconds();
        for (int i = 1; i < maxSessions - 1; i++) {
            account.startSession();
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
        return factory.create(command).orElseThrow(result ->
                new IllegalStateException(
                        "AccountMother failed to create account:\n" + result
                )
        );
    }

    private static class FakePasswordHasher implements PasswordHasher {
        @Override public String hash(String raw) { return "hashed_" + raw; }
        @Override public boolean verify(String raw, String hashed) {
            return hashed.equals("hashed_" + raw);
        }
    }
}