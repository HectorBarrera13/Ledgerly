package toast.appback.src.debts.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import toast.appback.src.debts.domain.event.DebtCreated;

public class DebtCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(DebtCreatedListener.class);

    @EventListener
    public void handle(DebtCreated event) {
        log.info("Debt created: {}", event);
    }
}
