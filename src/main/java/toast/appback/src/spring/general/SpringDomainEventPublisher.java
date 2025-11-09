package toast.appback.src.spring.general;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.application.EventBus;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements EventBus {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}
