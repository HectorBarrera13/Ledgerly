package toast.appback.src.auth.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import toast.appback.src.auth.domain.event.SessionAdded;

@Component
public class SessionAddedListener {

    private static final Logger LOG = LoggerFactory.getLogger(SessionAddedListener.class);

    @EventListener
    public void handle(SessionAdded event) {
        LOG.info("New session added: {}", event);
    }
}
