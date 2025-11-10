package toast.appback.src.users.domain;

import java.util.Objects;

public class FriendShipId {
    private final Long value;

    public FriendShipId(Long value) {
        this.value = value;
    }

    public static FriendShipId load(Long id) {
        return new FriendShipId(id);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FriendShipId{" +
                "id=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FriendShipId that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
