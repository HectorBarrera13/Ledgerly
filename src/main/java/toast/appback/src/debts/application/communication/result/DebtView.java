package toast.appback.src.debts.application.communication.result;

import java.util.UUID;

public record DebtView(
        UUID debtId,
        String purpose,
        String description,
        Long amount,
        String currency,
        String debtorName,
        String creditorName,
        String status
) {

}
