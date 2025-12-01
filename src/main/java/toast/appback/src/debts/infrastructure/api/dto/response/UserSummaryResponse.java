package toast.appback.src.debts.infrastructure.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UserSummaryResponse(
        @JsonProperty("user_id")
        UUID userId,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName
) {
}