package toast.appback.src.users.domain;

import java.time.Instant;

public record Friend(UserId friendId, Instant addedAt) {

    public static Friend create(UserId friendId) {
        return new Friend(friendId, Instant.now());
    }
}
