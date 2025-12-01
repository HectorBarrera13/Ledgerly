package toast.appback.src.debts.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import toast.appback.src.debts.domain.event.DebtAccepted;

@Component
public class DebtAcceptedListener {

    private static final Logger log = LoggerFactory.getLogger(DebtAcceptedListener.class);

    @EventListener
    public void handle(DebtAccepted event) {
        log.info("Debt accepted: {}", event);
    }
}
