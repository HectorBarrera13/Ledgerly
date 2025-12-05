package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.application.UseCaseFunction;
import toast.appback.src.users.application.communication.command.EditUserCommand;
import toast.appback.src.users.application.communication.result.UserView;

/**
 * Contrato del caso de uso para editar los datos de un usuario.
 * <p>
 * Implementaciones deben aplicar las modificaciones y devolver la vista actualizada.
 */
public interface EditUser extends UseCaseFunction<UserView, EditUserCommand> {
}
