package toast.appback.src.auth.domain;

import java.util.UUID;

public record AccountId(UUID id) {
    public static AccountId create(UUID id) {
        return new AccountId(id);
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }
}
