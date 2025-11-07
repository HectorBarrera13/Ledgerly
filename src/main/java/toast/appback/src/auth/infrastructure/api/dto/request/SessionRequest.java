package toast.appback.src.auth.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SessionRequest(
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
