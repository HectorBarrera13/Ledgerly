package toast.appback.src.shared.infrastructure.spring.general;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.application.DomainEventBus;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventBus {

    private final ApplicationEventPublisher applicationEventPublisher;

    private void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}
