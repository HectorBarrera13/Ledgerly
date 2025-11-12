package toast.appback.src.users.domain.vos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.users.domain.FriendShipId;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FriendShipId Value Object Test")
public class FriendShipIdTest {

    @Test
    @DisplayName("Should create FriendShipId from Long")
    void testCreateFriendShipIdFromLong() {
        Long idValue = 12345L;
        FriendShipId friendShipId = FriendShipId.load(idValue);
        assertEquals(idValue, friendShipId.getValue());
    }

    @Test
    @DisplayName("Should correctly compare two FriendShipId instances")
    void testFriendShipIdEquality() {
        Long idValue = 12345L;
        FriendShipId friendShipId1 = FriendShipId.load(idValue);
        FriendShipId friendShipId2 = FriendShipId.load(idValue);
        assertEquals(friendShipId1, friendShipId2);
    }

}
