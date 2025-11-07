package toast.appback.src.auth.domain;

import java.util.UUID;

public record SessionId(UUID id) {

    public static SessionId create(UUID id) {
        return new SessionId(id);
    }

    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }
}
