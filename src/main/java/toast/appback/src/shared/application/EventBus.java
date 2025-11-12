package toast.appback.src.shared.application;

import toast.appback.src.shared.domain.DomainEvent;

import java.util.List;

public interface EventBus {
    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
}
