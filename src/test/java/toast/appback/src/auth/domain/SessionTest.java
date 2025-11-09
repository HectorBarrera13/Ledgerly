package toast.appback.src.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Session Domain Test")
public class SessionTest {

    private final SessionId sessionId = new SessionId(UUID.randomUUID());
    private final Session session = Session.create(sessionId);

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
        Session anotherSession = Session.create(sessionId);
        assertEquals(session, anotherSession);
    }

    @Test
    @DisplayName("Should not be equal when having different session ids")
    void testSessionInequality() {
        Session anotherSession = Session.create(new SessionId(UUID.randomUUID()));
        assertNotEquals(session, anotherSession);
    }
}
