package toast.appback.src.quickDebt.domain;

import java.util.UUID;

public record QuickDebtId(UUID uuid) {

    public static QuickDebtId generateQuickDebtId() {
        return new QuickDebtId(UUID.randomUUID());
    }

    public static QuickDebtId load(UUID uuid) {
        return new QuickDebtId(uuid);
    }
}
