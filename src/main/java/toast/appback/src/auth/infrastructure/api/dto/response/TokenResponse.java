package toast.appback.src.auth.infrastructure.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("expires_at")
        Instant expiresAt,
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
