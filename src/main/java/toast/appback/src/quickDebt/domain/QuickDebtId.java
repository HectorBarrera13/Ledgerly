package toast.appback.src.quickDebt.domain;

import java.util.Objects;
import java.util.UUID;

public class QuickDebtId {
    private final UUID id;

    private QuickDebtId(UUID id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        QuickDebtId that = (QuickDebtId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static QuickDebtId generateQuickDebtId() {
        return new QuickDebtId(UUID.randomUUID());
    }

    public static QuickDebtId load(UUID uuid) {
        return new QuickDebtId(uuid);
    }
}
