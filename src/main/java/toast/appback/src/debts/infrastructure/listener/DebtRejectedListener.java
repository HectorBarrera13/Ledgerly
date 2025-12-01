package toast.appback.src.debts.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import toast.appback.src.debts.domain.event.DebtRejected;

@Component
public class DebtRejectedListener {

    private static final Logger log = LoggerFactory.getLogger(DebtRejectedListener.class);

    @EventListener
    public void handle(DebtRejected event) {
        log.info("Debt rejected: {}", event);
    }
}
