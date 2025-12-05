package toast.appback.src.users.domain.event;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.UserId;

/**
 * Evento de dominio que indica que se ha creado una relación de amistad entre dos usuarios.
 *
 * @param firstUser  Identificador del usuario que inició la amistad.
 * @param secondUser Identificador del usuario receptor.
 */
public record FriendAdded(UserId firstUser, UserId secondUser) implements DomainEvent {
}
