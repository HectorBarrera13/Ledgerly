package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.application.UseCaseFunction;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.domain.User;

/**
 * Contrato del caso de uso para crear un usuario.
 * <p>
 * Implementaciones deben crear el usuario a partir de los datos del comando y
 * devolver la entidad de dominio creada.
 */
public interface CreateUser extends UseCaseFunction<User, CreateUserCommand> {
}
