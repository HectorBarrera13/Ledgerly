package toast.appback.src.users.domain;

import java.util.UUID;

public record UserId(UUID value) {

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId load(UUID uuid) {
        return new UserId(uuid);
    }
}
