package toast.appback.src.auth.application.communication.result;

import java.time.Instant;

public record TokenInfo(
        String value,
        String type,
        Instant expiresAt
) {
}
