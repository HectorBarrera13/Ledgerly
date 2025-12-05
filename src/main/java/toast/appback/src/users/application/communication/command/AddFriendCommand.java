package toast.appback.src.users.application.communication.command;

import toast.appback.src.users.domain.UserId;

/**
 * Comando para solicitar la creación de una relación de amistad entre dos usuarios.
 *
 * @param userAId Identificador del usuario que inicia la solicitud.
 * @param userBId Identificador del usuario receptor de la solicitud.
 */
public record AddFriendCommand(
        UserId userAId,
        UserId userBId
) {
}
