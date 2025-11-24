package toast.appback.src.debts.application.communication.result;

import java.util.UUID;

public record DebtBaseView(
        UUID debtId,
        String purpose,
        String description,
        Long amount,
        String currency,
        String status,
        String debtType
) implements DebtView {

    public String getDebtType() {
        return debtType;
    }
}
