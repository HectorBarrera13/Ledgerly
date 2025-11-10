package toast.appback.src.auth.domain;

import java.util.Objects;
import java.util.UUID;

public class AccountId {

    private final UUID value;

    private AccountId(UUID value) {
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    public static AccountId load(UUID uuid) {
        return new AccountId(uuid);
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "AccountId{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AccountId accountId)) return false;
        return Objects.equals(value, accountId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
