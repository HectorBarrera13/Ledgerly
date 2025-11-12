package toast.appback.src.users.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainEvent;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Domain Tests")
public class UserTest {
    private final UUID uuid = UUID.randomUUID();
    private final String FIRST_NAME = "John";
    private final String LAST_NAME = "Doe";
    private final String PHONE_COUNTRY_CODE = "+52";
    private final String PHONE_NUMBER = "9341341";

    private final User user = new User(
            UserId.load(uuid),
            Name.load(FIRST_NAME, LAST_NAME),
            Phone.load(PHONE_COUNTRY_CODE, PHONE_NUMBER)
    );

    @Test
    @DisplayName("Should return all user data correctly")
    void testUserData() {
        assertEquals(uuid, user.getUserId().getValue());
        assertEquals(FIRST_NAME, user.getName().getFirstName());
        assertEquals(LAST_NAME, user.getName().getLastName());
        assertEquals(PHONE_COUNTRY_CODE, user.getPhone().getCountryCode());
        assertEquals(PHONE_NUMBER, user.getPhone().getNumber());
    }

    @Test
    @DisplayName("Should be equal when having the same id")
    void testUserEquality() {
        User anotherUser = new User(
                UserId.load(uuid),
                Name.load("Jane", "Smith"),
                Phone.load("+1", "1234567")
        );
        assertEquals(user, anotherUser);
    }

    @Test
    @DisplayName("Should change name correctly")
    void testChangeName() {
        Name newName = Name.load("Alice", "Johnson");
        user.changeName(newName);
        assertEquals("Alice", user.getName().getFirstName());
        assertEquals("Johnson", user.getName().getLastName());
    }

    @Test
    @DisplayName("Should record and pull domain events correctly")
    void testDomainEvents() {
        DomainEvent event = new DomainEvent() {
            @Override
            public String toString() {
                return "TestEvent";
            }
        };
        user.recordEvent(event);
        assertEquals(1, user.pullEvents().size());
        assertEquals(0, user.pullEvents().size());
    }

    @Test
    @DisplayName("toString should return correct representation")
    void testToString() {
        String expected = "User{" +
                "accountId=" + user.getUserId() +
                ", name=" + user.getName() +
                ", phone=" + user.getPhone() +
                ", domainEvents=[]" +
                '}';
        assertEquals(expected, user.toString());
    }

    @Test
    @DisplayName("Should not be equal when having different ids")
    void testUserInequality() {
        User anotherUser = new User(
                UserId.load(UUID.randomUUID()),
                Name.load("Jane", "Smith"),
                Phone.load("+1", "1234567")
        );
        assertNotEquals(user, anotherUser);
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

        assertEquals(2, user.pullEvents().size());
        assertEquals(0, user.pullEvents().size());
    }
}
