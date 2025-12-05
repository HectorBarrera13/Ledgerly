package toast.appback.src.auth.application.communication.result;

/**
 * Pares de tokens devueltos tras un proceso de autenticación.
 *
 * @param accessToken  Token de acceso (JWT) con tiempo de expiración limitado.
 * @param refreshToken Token de refresco (JWT) para renovar el access token.
 */
public record Tokens(
        Jwt accessToken,
        Jwt refreshToken
) {
}
