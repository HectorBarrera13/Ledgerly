package toast.appback.src.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Account Domain Test")
public class AccountTest {

    private final UUID uuid = UUID.randomUUID();
    private final String EMAIL = "example@gmail.com";
    private final String PASSWORD_HASH = "hashedPassword123";

    private final Account account = new Account(
            AccountId.load(uuid),
            UserId.load(uuid),
            Email.load(EMAIL),
            Password.fromHashed(PASSWORD_HASH)
    );

    @Test
    @DisplayName("Should return all account data correctly")
    void testAccountData() {
        assertEquals(uuid, account.getAccountId().getValue());
        assertEquals(uuid, account.getUserId().getValue());
        assertEquals(EMAIL, account.getEmail().getValue());
        assertEquals(PASSWORD_HASH, account.getPassword().getHashed());
    }

    @Test
    @DisplayName("Should be equal when having the same account id and user id")
    void testAccountEquality() {
        Account anotherAccount = new Account(
                AccountId.load(uuid),
                UserId.load(uuid),
                Email.load(EMAIL),
                Password.fromHashed("ADAWDAW")
        );
        assertEquals(account, anotherAccount);
    }

    @Test
    @DisplayName("Should not be equal when having different account ids")
    void testAccountInequality() {
        Account anotherAccount = new Account(
                AccountId.load(UUID.randomUUID()),
                UserId.load(uuid),
                Email.load(EMAIL),
                Password.fromHashed(PASSWORD_HASH)
        );
        assertNotEquals(account, anotherAccount);
    }

    @Test
    @DisplayName("Should add session correctly when under limit")
    void testAddSessionUnderLimit() {
        for (int i = 0; i < 5; i++) {
            Session session = new Session(SessionId.generate(), SessionStatus.NORMAL, null);
            var result = account.addSession(session);
            assertTrue(result.isSuccess());
        }
    }

    @Test
    @DisplayName("Should not add session when limit exceeded")
    void testAddSessionOverLimit() {
        for (int i = 0; i < 5; i++) {
            Session session = new Session(SessionId.generate(), SessionStatus.NORMAL, null);
            account.addSession(session);
        }
        Session extraSession = new Session(SessionId.generate(), SessionStatus.NORMAL, null);
        var result = account.addSession(extraSession);
        assertTrue(result.isFailure());
        assertEquals("session limit exceeded", result.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Should revoke session correctly")
    void testRevokeSession() {
        Session session = new Session(SessionId.generate(), SessionStatus.NORMAL, null);
        account.addSession(session);
        var revokeResult = account.revokeSession(session.getSessionId());
        assertTrue(revokeResult.isSuccess());
        assertTrue(session.isRevoked());
    }

    @Test
    @DisplayName("Should not revoke non-existent session")
    void testRevokeNonExistentSession() {
        var revokeResult = account.revokeSession(SessionId.generate());
        assertTrue(revokeResult.isFailure());
        assertEquals("session not found", revokeResult.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Should revoke all sessions correctly")
    void testRevokeAllSessions() {
        for (int i = 0; i < 3; i++) {
            Session session = new Session(SessionId.generate(), SessionStatus.NORMAL, null);
            account.addSession(session);
        }
        account.revokeAllSessions();
        for (Session session : account.getSessions()) {
            assertTrue(session.isRevoked());
        }
    }

    @Test
    @DisplayName("Should toString return correct representation")
    void testToString() {
        String expected = "Account{" +
                "accountId=" + account.getAccountId() +
                ", userId=" + account.getUserId() +
                ", email=" + account.getEmail() +
                ", password=" + account.getPassword() +
                ", sessions=" + account.getSessions() +
                ", domainEvents=" + account.pullEvents() +
                '}';
        assertEquals(expected, account.toString());
    }

    @Test
    @DisplayName("Should generate domain events on session actions")
    void testDomainEventsOnSessionActions() {
        Session session = new Session(SessionId.generate(), SessionStatus.NORMAL, null);
        account.addSession(session);
        assertEquals(1, account.pullEvents().size());

        account.revokeSession(session.getSessionId());
        assertEquals(1, account.pullEvents().size());
    }
}
