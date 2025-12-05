package toast.appback.src.groups.domain.event;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.shared.domain.DomainEvent;

/**
 * Evento que indica que un grupo fue creado.
 *
 * @param groupId          Identificador del grupo creado.
 * @param groupInformation Información inicial del grupo.
 */
public record GroupCreated(
        GroupId groupId,
        GroupInformation groupInformation
) implements DomainEvent {
}
