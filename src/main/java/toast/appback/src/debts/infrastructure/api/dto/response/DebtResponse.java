package toast.appback.src.debts.infrastructure.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record DebtResponse(
        UUID id,
        String purpose,
        String description,
        String currency,
        Long amount,
        @JsonProperty("debtor_name")
        String debtorName,
        @JsonProperty("creditor_name")
        String creditorName,
        String status
) {
}
