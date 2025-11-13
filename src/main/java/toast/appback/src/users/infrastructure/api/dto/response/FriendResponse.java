package toast.appback.src.users.infrastructure.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record FriendResponse(
        UUID id,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        String phone,
        @JsonProperty("added_at")
        Instant addedAt
) {
}
