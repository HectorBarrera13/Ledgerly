package toast.appback.src.auth.application.communication.result;

import java.time.Instant;

/**
 * Representación de un token JWT con su valor y fecha de expiración.
 *
 * @param value     Cadena del token JWT.
 * @param expiresAt Momento en que expira el token.
 */
public record Jwt(
        String value,
        Instant expiresAt
) {
}
