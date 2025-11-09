package toast.appback.src.auth.domain;

import java.util.UUID;

public record SessionId(UUID value) {

    public static SessionId load(UUID id) {
        return new SessionId(id);
    }

    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }
}
