package toast.appback.src.auth.infrastructure.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record RefreshTokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken,
        @JsonProperty("session_duration")
        long sessionDuration,
        @JsonProperty("session_expires_at")
        Instant sessionExpiresAt
) {
}
