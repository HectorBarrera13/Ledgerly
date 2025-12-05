package toast.appback.src.groups.application.communication.command;

import toast.appback.src.users.domain.UserId;

/**
 * Comando para crear un nuevo grupo.
 *
 * @param name        Nombre del grupo.
 * @param description Descripción del grupo.
 * @param creatorId   Identificador del usuario creador.
 */
public record CreateGroupCommand(
        String name,
        String description,
        UserId creatorId
) {
}
