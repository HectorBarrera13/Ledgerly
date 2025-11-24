package toast.appback.src.debts.infrastructure.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record QuickDebtResponse(
        UUID id,
        String purpose,
        String description,
        BigDecimal amount,
        String currency,
        String status,
        UserSummaryResponse userSummary,
        String role,
        String targetUserName
) implements  DebtResponseInt {}
