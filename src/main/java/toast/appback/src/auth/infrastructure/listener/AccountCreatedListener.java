package toast.appback.src.auth.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import toast.appback.src.auth.domain.event.AccountCreated;

@Component
@RequiredArgsConstructor
public class AccountCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(AccountCreatedListener.class);

    @EventListener
    public void handle(AccountCreated event) {
        log.info("Handling AccountCreated event for accountId: {}", event.accountId().getValue());
    }
}
