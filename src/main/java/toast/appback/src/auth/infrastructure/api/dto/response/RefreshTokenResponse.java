package toast.appback.src.auth.infrastructure.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record RefreshTokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_at")
        Instant expiresAt
) {
}
