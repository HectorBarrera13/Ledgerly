package toast.appback.src.auth.domain;

import java.time.Instant;

public record Token(
    String value,
    String tokenType,
    Instant expiresAt
) {
    public static Token create(String token, String tokenType, Instant expiresAt) {
        return new Token(token, tokenType, expiresAt);
    }

    public boolean isValid() {
        return Instant.now().isBefore(expiresAt);
    }
}
