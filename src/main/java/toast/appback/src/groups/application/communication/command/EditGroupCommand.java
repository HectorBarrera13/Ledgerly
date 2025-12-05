package toast.appback.src.groups.application.communication.command;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

/**
 * Comando para editar la información básica de un grupo.
 *
 * @param groupId     Identificador del grupo a editar.
 * @param actorId     Usuario que realiza la edición (debe tener permisos).
 * @param name        Nuevo nombre del grupo.
 * @param description Nueva descripción del grupo.
 */
public record EditGroupCommand(
        GroupId groupId,
        UserId actorId,
        String name,
        String description
) {
}
