package toast.appback.src.shared;

import java.util.List;

public interface EventBus {
    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
}
