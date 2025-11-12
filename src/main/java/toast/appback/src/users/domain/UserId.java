package toast.appback.src.users.domain;

import java.util.Objects;
import java.util.UUID;

public class UserId {

    private final UUID value;

    private UserId(UUID value) {
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId load(UUID uuid) {
        return new UserId(uuid);
    }

    @Override
    public String toString() {
        return "UserId{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserId userId)) return false;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
