package toast.appback.src.auth.application.communication.result;

import java.time.Instant;

public record Jwt(
        String value,
        Instant expiresAt
) {
}
