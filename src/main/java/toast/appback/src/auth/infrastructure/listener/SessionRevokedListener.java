package toast.appback.src.auth.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import toast.appback.src.auth.domain.event.SessionRevoked;

@Component
public class SessionRevokedListener {

    private static final Logger log = LoggerFactory.getLogger(SessionRevokedListener.class);

    @EventListener
    public void handle(SessionRevoked event) {
        log.info("Session revoked: {}", event);
    }
}
