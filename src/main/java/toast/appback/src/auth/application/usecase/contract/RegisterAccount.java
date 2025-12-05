package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.shared.application.UseCaseFunction;

/**
 * Contrato del caso de uso para registrar un nuevo usuario y su cuenta simultáneamente.
 * <p>
 * Implementaciones deben crear el usuario, la cuenta, iniciar sesión y devolver un {@link AuthResult}.
 */
public interface RegisterAccount extends UseCaseFunction<AuthResult, RegisterAccountCommand> {
}