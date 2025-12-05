package toast.appback.src.users.application.communication.command;

import toast.appback.src.users.domain.UserId;

/**
 * Comando para eliminar una relación de amistad existente.
 *
 * @param requesterId Identificador del usuario que solicita la eliminación.
 * @param friendId    Identificador del amigo a eliminar.
 */
public record RemoveFriendCommand(
        UserId requesterId,
        UserId friendId
) {
}
