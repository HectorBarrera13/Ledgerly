package toast.appback.src.users.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import toast.appback.src.users.domain.event.UserCreated;

@Component
public class UserCreatedListener {

    private static final Logger logger = LoggerFactory.getLogger(UserCreatedListener.class);

    @EventListener
    public void handle(UserCreated event) {
        logger.info("User created: {}", event);
    }
}
