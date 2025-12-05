package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.shared.application.UseCaseFunction;

/**
 * Contrato del caso de uso que crea una cuenta para un usuario ya existente.
 * <p>
 * Implementaciones deben validar datos, crear la entidad `Account`, iniciar sesión inicial y
 * devolver un {@link CreateAccountResult} con la cuenta y la sesión.
 */
public interface CreateAccount extends UseCaseFunction<CreateAccountResult, CreateAccountCommand> {
}
