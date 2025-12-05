package toast.appback.src.users.domain.event;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.UserId;

/**
 * Evento de dominio que indica que un amigo ha sido eliminado de la lista de un usuario.
 *
 * @param userId   Identificador del usuario que pierde al amigo.
 * @param friendId Identificador del amigo removido.
 */
public record UserFriendRemoved(
        UserId userId,
        UserId friendId
) implements DomainEvent {
}
