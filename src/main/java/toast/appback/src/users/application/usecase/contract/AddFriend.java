package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.application.UseCaseFunction;
import toast.appback.src.users.application.communication.command.AddFriendCommand;
import toast.appback.src.users.application.communication.result.FriendView;

/**
 * Contrato del caso de uso para crear una relación de amistad entre dos usuarios.
 * <p>
 * Implementaciones deben validar la petición, crear la relación y devolver la vista del amigo creado.
 */
public interface AddFriend extends UseCaseFunction<FriendView, AddFriendCommand> {
}
