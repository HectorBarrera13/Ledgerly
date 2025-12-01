package toast.appback.src.shared.infrastructure.spring.general;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import toast.appback.src.shared.application.ApplicationEvent;
import toast.appback.src.shared.application.ApplicationEventBus;

@Component
@RequiredArgsConstructor
public class SpringApplicationEventPublisher implements ApplicationEventBus {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(ApplicationEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
