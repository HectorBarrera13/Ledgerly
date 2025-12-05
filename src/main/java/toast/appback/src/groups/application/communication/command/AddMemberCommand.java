package toast.appback.src.groups.application.communication.command;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

import java.util.List;

/**
 * Comando para agregar miembros a un grupo.
 *
 * @param groupId   Identificador del grupo al que se agregan los miembros.
 * @param actorId   Usuario que realiza la acción (debe tener permisos de administración del grupo).
 * @param membersId Lista de identificadores de usuarios a agregar.
 */
public record AddMemberCommand(
        GroupId groupId,
        UserId actorId,
        List<UserId> membersId
) {
}
