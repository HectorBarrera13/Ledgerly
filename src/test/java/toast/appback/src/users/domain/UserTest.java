package toast.appback.src.users.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.event.UserCreated;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static toast.appback.src.shared.DomainEventsUtils.assertContainsEventOfType;

@DisplayName("User Domain Tests")
class UserTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String PHONE_COUNTRY_CODE = "+52";
    private static final String PHONE_NUMBER = "9341341";
    private User user;

    @BeforeEach
    void setUp() {
        user = User.create(
                Name.load(FIRST_NAME, LAST_NAME),
                Phone.load(PHONE_COUNTRY_CODE, PHONE_NUMBER)
        );
    }

    @Test
    @DisplayName("Should return all user data correctly")
    void testUserData() {
        assertEquals(FIRST_NAME, user.getName().getFirstName());
        assertEquals(LAST_NAME, user.getName().getLastName());
        assertEquals(PHONE_COUNTRY_CODE, user.getPhone().getCountryCode());
        assertEquals(PHONE_NUMBER, user.getPhone().getNumber());
        List<DomainEvent> events = user.pullEvents();
        assertEquals(1, events.size());
        assertContainsEventOfType(events, UserCreated.class);
    }

    @Test
    @DisplayName("Should be equal when having the same id")
    void testUserEquality() {
        User anotherUser = User.load(
                user.getUserId(),
                Name.load("Jane", "Smith"),
                Phone.load("+1", "1234567"),
                Instant.now()
        );
        assertEquals(user, anotherUser);
    }

    @Test
    @DisplayName("Should not be equal when having different ids")
    void testUserInequality() {
        User anotherUser = User.create(
                Name.load("Jane", "Smith"),
                Phone.load("+1", "1234567")
        );
        assertNotEquals(user, anotherUser);
    }

    @Test
    @DisplayName("Should change name correctly")
    void testChangeName() {
        Name newName = Name.load("Alice", "Johnson");
        user.changeName(newName);
        assertEquals("Alice", user.getName().getFirstName());
        assertEquals("Johnson", user.getName().getLastName());
        assertEquals(1, user.pullEvents().size()); // Only creation event should be present
    }

    @Test
    @DisplayName("pullEvents should clear the domain events after pulling")
    void testPullEventsClearsEvents() {
        DomainEvent event1 = new DomainEvent() {
            @Override
            public String toString() {
                return "Event1";
            }
        };
        DomainEvent event2 = new DomainEvent() {
            @Override
            public String toString() {
                return "Event2";
            }
        };
        user.recordEvent(event1);
        user.recordEvent(event2);

        assertEquals(3, user.pullEvents().size());
        assertEquals(0, user.pullEvents().size());
    }
}
