package toast.appback.src.users.domain.vos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserId Value Object Test")
public class UserIdTest {

    @Test
    @DisplayName("Should create UserId from UUID")
    void testCreateUserIdFromUUID() {
        UUID uuid = UUID.randomUUID();
        UserId userId = UserId.load(uuid);
        assertEquals(uuid, userId.getValue());
    }

    @Test
    @DisplayName("Should generate a new UserId")
    void testGenerateUserId() {
        UserId userId1 = UserId.generate();
        UserId userId2 = UserId.generate();
        assertNotNull(userId1);
        assertNotNull(userId2);
        assertNotEquals(userId1, userId2);
    }

    @Test
    @DisplayName("Should correctly compare two UserId instances")
    void testUserIdEquality() {
        UUID uuid = UUID.randomUUID();
        UserId userId1 = UserId.load(uuid);
        UserId userId2 = UserId.load(uuid);
        assertEquals(userId1, userId2);
    }
}
