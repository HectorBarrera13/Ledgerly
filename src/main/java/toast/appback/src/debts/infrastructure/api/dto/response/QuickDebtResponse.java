package toast.appback.src.debts.infrastructure.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record QuickDebtResponse(
        UUID id,
        String purpose,
        String description,
        BigDecimal amount,
        String currency,
        String status,
        @JsonProperty("user_summary")
        UserSummaryResponse userSummary,
        String role,
        @JsonProperty("target_user_name")
        String targetUserName
) implements DebtResponseInt {
}
