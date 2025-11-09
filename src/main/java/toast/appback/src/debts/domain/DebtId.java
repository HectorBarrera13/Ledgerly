package toast.appback.src.debts.domain;

import java.util.UUID;

public record DebtId(UUID uuid){

    public static DebtId generateDebtId(){ return new DebtId(UUID.randomUUID()); }

    public static DebtId load(UUID uuid){ return new DebtId(uuid); }
}
