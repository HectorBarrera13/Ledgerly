package toast.appback.src.shared.application;

import java.time.Instant;

public interface ApplicationEvent {
    default Instant occurredOn() {
        return Instant.now();
    }
}
