package toast.appback.src.auth.application.communication.result;

import java.time.Instant;

public record TokenInfo(
        String token,
        String tokenType,
        Instant expiresAt
) {
}
