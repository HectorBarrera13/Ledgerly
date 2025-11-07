package toast.appback.src.auth.infrastructure.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record AccountLoginResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("session_expires_at")
        Instant sessionExpiresAt
) {
}
