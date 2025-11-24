package toast.appback.src.debts.infrastructure.api.dto.response;

import java.util.UUID;

public record DebtResponse(
        UUID id,
        String purpose,
        String description,
        Long amount,
        String currency,
        String status
) implements DebtResponseInt {
}
