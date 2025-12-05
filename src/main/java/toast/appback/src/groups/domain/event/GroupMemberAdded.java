package toast.appback.src.groups.domain.event;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.UserId;

/**
 * Evento que indica que un miembro fue agregado a un grupo.
 *
 * @param groupId Identificador del grupo.
 * @param userId  Identificador del usuario agregado.
 */
public record GroupMemberAdded(
        GroupId groupId,
        UserId userId
) implements DomainEvent {
}
