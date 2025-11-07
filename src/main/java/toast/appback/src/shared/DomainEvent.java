package toast.appback.src.shared;

import java.time.Instant;

public interface DomainEvent {
    default Instant occurredOn() {
        return Instant.now();
    }
}
