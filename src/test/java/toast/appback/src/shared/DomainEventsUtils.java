package toast.appback.src.shared;

import toast.appback.src.shared.domain.DomainEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DomainEventsUtils {

    public static <T extends DomainEvent> void assertContainsEventOfType(List<DomainEvent> events, Class<T> expectedType) {
        boolean contains = events.stream().anyMatch(expectedType::isInstance);
        assertTrue(contains, "Expected event of type " + expectedType.getSimpleName() + " not found in domain events.");
    }
}
