package toast.appback.src.auth.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import toast.appback.src.auth.domain.event.AccountCreated;

@Component
public class AccountCreatedListener {

    private static final Logger logger = LoggerFactory.getLogger(AccountCreatedListener.class);

    @EventListener
    public void handle(AccountCreated event) {
        logger.info("Account created: {}", event);
    }
}
