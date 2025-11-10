package toast.appback.src.auth.domain;

import java.util.Objects;
import java.util.UUID;

public class SessionId {

    private final UUID value;

    private SessionId(UUID value) {
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    public static SessionId load(UUID uuid) {
        return new SessionId(uuid);
    }

    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "SessionId{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SessionId sessionId)) return false;
        return Objects.equals(value, sessionId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
