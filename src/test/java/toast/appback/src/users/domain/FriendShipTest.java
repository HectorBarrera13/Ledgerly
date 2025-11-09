package toast.appback.src.users.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.DomainEvent;
import toast.appback.src.users.domain.event.FriendAdded;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FriendShip Domain Tests")
public class FriendShipTest {

    @Test
    @DisplayName("Should create a friendship between two users")
    void testCreateFriendship() {
        User requester = new User(
                UserId.load(UUID.randomUUID()),
                Name.load("Alice", "Smith"),
                Phone.load("+24", "1234567890")
        );

        User receiver = new User(
                UserId.load(UUID.randomUUID()),
                Name.load("Bob", "Johnson"),
                Phone.load("+24", "0987654321")
        );

        FriendShip friendShip = FriendShip.create(requester, receiver);
        assertNotNull(friendShip);
        assertEquals(requester, friendShip.getRequest());
        assertEquals(receiver, friendShip.getReceiver());
        List<DomainEvent> events = friendShip.pullEvents();
        assertEquals(1, events.size());
        assertInstanceOf(FriendAdded.class, events.getFirst());
    }
}