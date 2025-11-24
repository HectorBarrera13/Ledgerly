package toast.appback.src.debts.infrastructure.api.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record DebtBetweenUsersResponse(
        UUID id,
        String purpose,
        String description,
        BigDecimal amount,
        String currency,
        String status,
        @JsonProperty("debtor_summary")
        UserSummaryResponse debtorSummary,
        @JsonProperty("creditor_summary")
        UserSummaryResponse creditorSummary
) implements DebtResponseInt {
}


