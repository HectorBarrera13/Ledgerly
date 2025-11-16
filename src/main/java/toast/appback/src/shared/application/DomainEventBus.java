package toast.appback.src.shared.application;

import toast.appback.src.shared.domain.DomainEvent;

import java.util.List;

public interface DomainEventBus {
    void publishAll(List<DomainEvent> events);
}