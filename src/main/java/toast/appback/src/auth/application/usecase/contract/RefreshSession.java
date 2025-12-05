package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.result.Jwt;
import toast.appback.src.shared.application.UseCaseFunction;

/**
 * Contrato del caso de uso que renueva un access token a partir de un refresh token.
 * <p>
 * Implementaciones deben validar el refresh token, comprobar la sesión y devolver un nuevo {@link Jwt}.
 */
public interface RefreshSession extends UseCaseFunction<Jwt, String> {
}
