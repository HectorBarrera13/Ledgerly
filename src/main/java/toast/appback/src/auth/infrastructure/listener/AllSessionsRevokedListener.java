package toast.appback.src.auth.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import toast.appback.src.auth.domain.event.AllSessionsRevoked;

@Component
public class AllSessionsRevokedListener {

    private static final Logger LOG = LoggerFactory.getLogger(AllSessionsRevokedListener.class);

    @EventListener
    public void handle(AllSessionsRevoked event) {
        LOG.info("All sessions revoked: {}", event);
    }
}
