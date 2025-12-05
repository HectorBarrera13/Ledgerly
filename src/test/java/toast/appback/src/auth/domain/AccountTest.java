package toast.appback.src.auth.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.domain.event.AccountCreated;
import toast.appback.src.auth.domain.event.SessionAdded;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static toast.appback.src.shared.DomainEventsUtils.assertContainsEventOfType;
import static toast.appback.src.shared.ValueObjectsUtils.assertBusinessRuleErrorExists;

@DisplayName("Account Domain Test")
class AccountTest {

    private static final String EMAIL = "example@gmail.com";
    private static final String PASSWORD_HASH = "hashedPassword123";
    private static final UserId USER_ID = UserId.generate();
    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.create(
                USER_ID,
                Email.load(EMAIL),
                Password.fromHashed(PASSWORD_HASH)
        );
    }

    @Test
    @DisplayName("Should return all account data correctly")
    void testAccountData() {
        assertEquals(USER_ID, account.getUserId());
        assertEquals(EMAIL, account.getEmail().getValue());
        assertEquals(PASSWORD_HASH, account.getPassword().getHashed());
        List<DomainEvent> events = account.pullEvents();
        assertEquals(1, events.size());
        assertContainsEventOfType(events, AccountCreated.class);
    }

    @Test
    @DisplayName("Should be equal when having the same account id and user id")
    void testAccountEquality() {
        AccountId accountId = account.getAccountId();
        Account anotherAccount = Account.load(
                accountId,
                account.getUserId(),
                Email.load(EMAIL),
                Password.fromHashed("ADAWDAW"),
                Instant.now(),
                List.of()
        );
        assertEquals(account, anotherAccount);
    }

    @Test
    @DisplayName("Should not be equal when having different account ids")
    void testAccountInequality() {
        Account anotherAccount = Account.create(
                USER_ID,
                Email.load(EMAIL),
                Password.fromHashed(PASSWORD_HASH)
        );
        assertNotEquals(account, anotherAccount);
    }

    @Test
    @DisplayName("Should start session correctly")
    void testStartSession() {
        var result = account.startSession();
        assertTrue(result.isOk());
        Session session = result.get();
        assertNotNull(session);
        assertFalse(session.isRevoked());
        List<DomainEvent> events = account.pullEvents();
        assertEquals(2, events.size()); // AccountCreated + SessionAdded
        assertContainsEventOfType(events, AccountCreated.class);
        assertContainsEventOfType(events, SessionAdded.class);
    }

    @Test
    @DisplayName("Should add session correctly when under limit")
    void testStartSessionUnderLimit() {
        for (int i = 0; i < 5; i++) {
            var result = account.startSession();
            assertTrue(result.isOk());
        }
    }

    @Test
    @DisplayName("Should not add session when limit exceeded")
    void testStartSessionOverLimit() {
        for (int i = 0; i < 10; i++) {
            account.startSession();
        }
        var result = account.startSession();
        assertTrue(result.isFailure());
        assertBusinessRuleErrorExists(result.getErrors(), AccountBusinessCode.SESSION_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("Should revoke session correctly")
    void testRevokeSession() {
        Result<Session, DomainError> startedSession = account.startSession();
        Session session = startedSession.get();
        var revokeResult = account.revokeSession(session.getSessionId());
        assertTrue(revokeResult.isOk());
        assertTrue(session.isRevoked());
    }

    @Test
    @DisplayName("Should not revoke non-existent session")
    void testRevokeNonExistentSession() {
        var revokeResult = account.revokeSession(SessionId.generate());
        assertTrue(revokeResult.isFailure());
        assertBusinessRuleErrorExists(revokeResult.getErrors(), AccountBusinessCode.SESSION_NOT_FOUND);
    }

    @Test
    @DisplayName("Should not revoke already revoked session")
    void testRevokeAlreadyRevokedSession() {
        Result<Session, DomainError> startedSession = account.startSession();
        Session session = startedSession.get();
        account.revokeSession(session.getSessionId());
        var revokeAgainResult = account.revokeSession(session.getSessionId());
        assertTrue(revokeAgainResult.isFailure());
        assertBusinessRuleErrorExists(revokeAgainResult.getErrors(), AccountBusinessCode.SESSION_ALREADY_REVOKED);
    }

    @Test
    @DisplayName("Should revoke all sessions correctly")
    void testRevokeAllSessions() {
        for (int i = 0; i < 3; i++) {
            account.startSession();
        }
        account.revokeAllSessions();
        for (Session session : account.getSessions()) {
            assertTrue(session.isRevoked());
        }
    }

    @Test
    @DisplayName("Should return correct errors when verifying session status")
    void testVerifyValidSession() {
        Result<Session, DomainError> startedSession = account.startSession();
        Session session = startedSession.get();

        // Valid session
        var validResult = account.verifySession(session.getSessionId());
        assertTrue(validResult.isOk());

        // Revoke session and verify
        account.revokeSession(session.getSessionId());
        var revokedResult = account.verifySession(session.getSessionId());
        assertTrue(revokedResult.isFailure());
        List<DomainError> errors = revokedResult.getErrors();
        assertBusinessRuleErrorExists(errors, AccountBusinessCode.SESSION_REVOKED);

        // Verify non-existent session
        var nonExistentResult = account.verifySession(SessionId.generate());
        assertTrue(nonExistentResult.isFailure());
        assertBusinessRuleErrorExists(nonExistentResult.getErrors(), AccountBusinessCode.SESSION_NOT_FOUND);
    }

    @Test
    @DisplayName("Should generate domain events on session actions")
    void testDomainEventsOnSessionActions() {
        Result<Session, DomainError> startedSession = account.startSession();
        Session session = startedSession.get();
        assertEquals(2, account.pullEvents().size());

        account.revokeSession(session.getSessionId());
        assertEquals(1, account.pullEvents().size());
    }
}
