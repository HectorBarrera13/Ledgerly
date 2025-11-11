package toast.appback.src.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Session Domain Test")
public class SessionTest {

    private final SessionId sessionId = SessionId.generate();
    private final Session session = Session.load(sessionId, SessionStatus.NORMAL, Instant.now());

    @Test
    @DisplayName("Should create session with NORMAL status")
    void testCreateSession() {
        assertEquals(sessionId, session.getSessionId());
        assertEquals(SessionStatus.NORMAL, session.getStatus());
    }

    @Test
    @DisplayName("Should revoke session correctly")
    void testRevokeSession() {
        session.revoke();
        assertEquals(SessionStatus.REVOKED, session.getStatus());
    }

    @Test
    @DisplayName("Should check session validity correctly")
    void testSessionValidity() {
        assertTrue(session.isValid());
        session.revoke();
        assertFalse(session.isValid());
    }

    @Test
    @DisplayName("Should be equal when having the same session id")
    void testSessionEquality() {
        Session anotherSession = Session.load(session.getSessionId(), SessionStatus.NORMAL, Instant.now());
        assertEquals(session, anotherSession);
    }

    @Test
    @DisplayName("Should not be equal when having different session ids")
    void testSessionInequality() {
        Session anotherSession = Session.create();
        assertNotEquals(session, anotherSession);
    }
}
