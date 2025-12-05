package toast.appback.src.users.application.usecase.contract;

import toast.appback.src.shared.application.UseCaseProcedure;
import toast.appback.src.users.application.communication.command.RemoveFriendCommand;

/**
 * Contrato del caso de uso para eliminar una relación de amistad.
 * <p>
 * Implementaciones deben validar permisos, eliminar la relación y publicar los eventos necesarios.
 */
public interface RemoveFriend extends UseCaseProcedure<RemoveFriendCommand> {
}
