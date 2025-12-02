package toast.appback.src.auth.domain.vos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.domain.SessionId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SessionId Value Object Test")
class SessionIdTest {

    @Test
    @DisplayName("Should create SessionId from UUID")
    void testCreateSessionIdFromUUID() {
        UUID uuid = UUID.randomUUID();
        SessionId sessionId = SessionId.load(uuid);
        assertEquals(uuid, sessionId.getValue());
    }

    @Test
    @DisplayName("Should generate a new SessionId")
    void testGenerateSessionId() {
        SessionId sessionId1 = SessionId.generate();
        SessionId sessionId2 = SessionId.generate();
        assertNotNull(sessionId1);
        assertNotNull(sessionId2);
        assertNotEquals(sessionId1, sessionId2);
    }

    @Test
    @DisplayName("Should correctly compare two SessionId instances")
    void testSessionIdEquality() {
        UUID uuid = UUID.randomUUID();
        SessionId sessionId1 = SessionId.load(uuid);
        SessionId sessionId2 = SessionId.load(uuid);
        assertEquals(sessionId1, sessionId2);
    }
}
