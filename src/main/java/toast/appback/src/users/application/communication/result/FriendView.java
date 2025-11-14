package toast.appback.src.users.application.communication.result;

import java.time.Instant;
import java.util.UUID;

public record FriendView(
        UUID userId,
        String firstName,
        String lastName,
        String phone,
        Instant addedAt
) {
}
