package toast.appback.src.auth.application.port;

import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.Jwt;
import toast.appback.src.auth.application.communication.result.Tokens;

/**
 * Servicio que encapsula la generación y extracción de tokens JWT dentro de la aplicación.
 * <p>
 * Implementaciones deben proporcionar generación de access/refresh tokens y extracción segura
 * de claims desde dichos tokens.
 */
public interface TokenService {
    /**
     * Genera un par de tokens (access + refresh) a partir de las claims proporcionadas.
     *
     * @param tokenClaims Datos que se incluirán en los tokens.
     * @return Objeto {@link Tokens} con accessToken y refreshToken.
     */
    Tokens generateTokens(TokenClaims tokenClaims);

    /**
     * Genera solo un access token a partir de las claims proporcionadas.
     *
     * @param tokenClaims Datos que se incluirán en el token.
     * @return JWT representando el token de acceso.
     */
    Jwt generateAccessToken(TokenClaims tokenClaims);

    /**
     * Extrae las claims desde un refresh token (validado).
     *
     * @param refreshToken Token de refresco.
     * @return Claims extraídas incluyendo accountId, userId y sessionId.
     */
    TokenClaims extractClaimsFromRefreshToken(String refreshToken);

    /**
     * Extrae claims desde un access token sin validación estricta (uso interno y con precaución).
     *
     * @param refreshToken Token de acceso (posiblemente sin validar).
     * @return Claims extraídas.
     */
    TokenClaims extractClaimsFromAccessTokenUnsafe(String refreshToken);

    /**
     * Extrae y valida las claims desde un access token.
     *
     * @param accessToken Token de acceso.
     * @return Claims validadas.
     */
    TokenClaims extractClaimsFromAccessToken(String accessToken);
}