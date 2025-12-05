package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.shared.application.UseCaseFunction;

/**
 * Contrato del caso de uso para autenticar una cuenta con credenciales.
 * <p>
 * Implementaciones deben validar credenciales y devolver un {@link AuthResult} con información de cuenta, usuario y tokens.
 */
public interface AuthenticateAccount extends UseCaseFunction<AuthResult, AuthenticateAccountCommand> {
}
