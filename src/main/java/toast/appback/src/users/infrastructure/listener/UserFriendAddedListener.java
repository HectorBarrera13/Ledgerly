package toast.appback.src.users.infrastructure.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import toast.appback.src.users.domain.event.FriendAdded;

@Component
public class UserFriendAddedListener {

    private static final Logger log = LoggerFactory.getLogger(UserFriendAddedListener.class);

    @EventListener
    public void handle(FriendAdded event) {
        log.info("User friend added: {}", event);
    }
}
