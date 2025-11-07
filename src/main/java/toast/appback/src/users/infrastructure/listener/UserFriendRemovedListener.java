package toast.appback.src.users.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import toast.appback.src.users.domain.event.UserFriendRemoved;

@Component
public class UserFriendRemovedListener {

    private static final Logger log = LoggerFactory.getLogger(UserFriendRemovedListener.class);

    @EventListener
    public void handle(UserFriendRemoved event) {
        log.info("User friend removed: {}", event);
    }
}
