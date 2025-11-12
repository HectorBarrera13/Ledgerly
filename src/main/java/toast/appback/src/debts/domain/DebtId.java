package toast.appback.src.debts.domain;

import java.util.Objects;
import java.util.UUID;

public class DebtId {
    private final UUID id;

    private DebtId(UUID id) {
        this.id = id;
    }

    public UUID getValue() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        DebtId debtId = (DebtId) o;
        return Objects.equals(id, debtId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static DebtId generate() {
        return new DebtId(UUID.randomUUID());
    }

    public static DebtId load(UUID uuid){ return new DebtId(uuid); }

}
