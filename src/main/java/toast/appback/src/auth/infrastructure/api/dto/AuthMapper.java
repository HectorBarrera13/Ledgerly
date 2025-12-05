package toast.appback.src.auth.infrastructure.api.dto;

import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.auth.infrastructure.api.dto.response.AccountResponse;
import toast.appback.src.auth.infrastructure.api.dto.response.AuthResponse;
import toast.appback.src.auth.infrastructure.api.dto.response.TokenResponse;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;

/**
 * Mapeador estático que convierte resultados de autenticación y vistas de cuenta
 * en DTOs de respuesta para la API.
 * <p>
 * Responsabilidad:
 * - Crear objetos {@link AuthResponse}, {@link AccountResponse} y {@link TokenResponse}
 * a partir de las vistas de la capa de aplicación ({@link AuthResult}, {@link AccountView}).
 * <p>
 * Notas:
 * - El método {@link #authToResponse(AuthResult, boolean)} permite excluir el refresh token
 * de la respuesta cuando {@code excludeRefreshToken} es true (por motivos de seguridad).
 * - Los métodos son puros y no modifican los objetos de entrada.
 */
public class AuthMapper {

    private AuthMapper() {
    }

    /**
     * Convierte un {@link AuthResult} del caso de uso de autenticación a la representación
     * que devuelve la API.
     *
     * @param result              Resultado del caso de uso de autenticación que contiene cuenta, usuario y tokens.
     * @param excludeRefreshToken Si es true, el campo del refresh token en la respuesta será null.
     *                            Útil cuando no se desea devolver el refresh token en ciertas respuestas.
     * @return {@link AuthResponse} con la información de cuenta, usuario y tokens (posiblemente sin refresh token).
     */
    public static AuthResponse authToResponse(AuthResult result, boolean excludeRefreshToken) {
        AccountResponse accountResponse = new AccountResponse(
                result.account().accountId(),
                result.account().email()
        );
        UserResponse userResponse = new UserResponse(
                result.user().userId(),
                result.user().firstName(),
                result.user().lastName(),
                result.user().phone()
        );
        TokenResponse tokenResponse = new TokenResponse(
                result.tokens().accessToken().value(),
                result.tokens().accessToken().expiresAt(),
                excludeRefreshToken ? null : result.tokens().refreshToken().value()
        );
        return new AuthResponse(
                accountResponse,
                userResponse,
                tokenResponse
        );
    }

    /**
     * Convierte una vista de cuenta {@link AccountView} a su DTO {@link AccountResponse}.
     *
     * @param accountView Vista de cuenta (identificador y email).
     * @return {@link AccountResponse} con los campos públicos de la cuenta.
     */
    public static AccountResponse toAccountResponse(AccountView accountView) {
        return new AccountResponse(
                accountView.accountId(),
                accountView.email()
        );
    }
}
