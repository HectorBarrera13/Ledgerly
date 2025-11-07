package toast.appback.src.users.domain;

import java.util.UUID;

public record UserId(UUID uuid) {

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId create(UUID uuid) {
        return new UserId(uuid);
    }
}
