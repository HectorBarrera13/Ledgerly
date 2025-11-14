package toast.appback.src.users.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.event.FriendAdded;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FriendShip Domain Tests")
public class FriendShipTest {

    @Test
    @DisplayName("Should create a friendship between two users")
    void testCreateFriendship() {
        User firstUser = new User(
                UserId.load(UUID.randomUUID()),
                Name.load("Alice", "Smith"),
                Phone.load("+24", "1234567890")
        );

        User secondUser = new User(
                UserId.load(UUID.randomUUID()),
                Name.load("Bob", "Johnson"),
                Phone.load("+24", "0987654321")
        );

        FriendShip friendShip = FriendShip.create(firstUser.getUserId(), secondUser.getUserId());
        assertNotNull(friendShip);
        assertNotNull(friendShip.getCreatedAt());
        assertEquals(firstUser.getUserId(), friendShip.getFirstUser());
        assertEquals(secondUser.getUserId(), friendShip.getSecondUser());
        List<DomainEvent> events = friendShip.pullEvents();
        assertEquals(1, events.size());
        assertInstanceOf(FriendAdded.class, events.getFirst());
    }
}