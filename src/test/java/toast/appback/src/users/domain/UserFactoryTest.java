package toast.appback.src.users.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.ValidatorType;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.users.domain.event.UserCreated;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static toast.appback.src.shared.ValueObjectsUtils.*;

@DisplayName("UserFactory Domain Tests")
public class UserFactoryTest {

    private final UserFactory userFactory = new DefaultUser();

    @Test
    @DisplayName("Collect all errors when creating a user with invalid data")
    void testCreateUserWithInvalidData() {
        Result<User, DomainError> result = userFactory.create(
                "", // Invalid first name
                null, // Invalid last name
                "123", // Invalid phone country code
                "" // Invalid phone number
        );
        assertTrue(result.isFailure());
        assertEquals(4, result.getErrors().size(), "Expected 4 validation errors");
        assertErrorExistsForField(result.getErrors(), ValidatorType.EMPTY_VALUE, "firstName");
        assertErrorExistsForField(result.getErrors(), ValidatorType.EMPTY_VALUE, "lastName");
        assertErrorExistsForField(result.getErrors(), ValidatorType.INVALID_FORMAT, "countryCode");
        assertErrorExistsForField(result.getErrors(), ValidatorType.EMPTY_VALUE, "number");
    }

    @Test
    @DisplayName("Collect some errors when creating a user with partially invalid data")
    void testCreateUserWithPartiallyInvalidData() {
        Result<User, DomainError> result = userFactory.create(
                "ValidFirstName",
                "", // Invalid last name
                "+1",
                "12" // Invalid phone number
        );
        assertTrue(result.isFailure());
        assertEquals(2, result.getErrors().size(), "Expected 2 validation errors");
        assertErrorExistsForField(result.getErrors(), ValidatorType.EMPTY_VALUE, "lastName");
        assertErrorExistsForField(result.getErrors(), ValidatorType.INVALID_FORMAT, "number");
    }

    @Test
    @DisplayName("Successfully create a user with valid data")
    void testCreateUserWithValidData() {
        Result<User, DomainError> result = userFactory.create(
                "John",
                "Doe",
                "+1",
                "1234567890"
        );
        assertTrue(result.isSuccess(), "Expected successful user creation");
        User user = result.getValue();
        assertEquals("John", user.getName().getFirstName());
        assertEquals("Doe", user.getName().getLastName());
        assertEquals("+1", user.getPhone().getCountryCode());
        assertEquals("1234567890", user.getPhone().getNumber());
    }

    @Test
    @DisplayName("Should generate a domain event upon user creation")
    void testUserCreationGeneratesDomainEvent() {
        Result<User, DomainError> result = userFactory.create(
                "Jane",
                "Smith",
                "+44",
                "9876543210"
        );
        assertTrue(result.isSuccess(), "Expected successful user creation");
        User user = result.getValue();
        List<DomainEvent> events = user.pullEvents();
        assertFalse(events.isEmpty(), "Expected domain events to be recorded upon user creation");
        DomainEvent event = events.getFirst();
        assertInstanceOf(UserCreated.class, event, "Expected a UserCreated domain event");
    }
}
