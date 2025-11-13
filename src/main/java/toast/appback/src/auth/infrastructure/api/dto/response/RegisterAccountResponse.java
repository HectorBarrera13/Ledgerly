package toast.appback.src.auth.infrastructure.api.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;

import java.time.Instant;

public record RegisterAccountResponse(
        UserResponse user,
        @JsonProperty("email")
        String email,
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("session_expires_at")
        Instant sessionExpiresAt
) {
}
