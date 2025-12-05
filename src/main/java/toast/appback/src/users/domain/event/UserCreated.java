package toast.appback.src.users.domain.event;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.Name;
import toast.appback.src.users.domain.UserId;

/**
 * Evento de dominio que indica que un usuario ha sido creado.
 *
 * @param userId Identificador del usuario creado.
 * @param name   Nombre del usuario al momento de la creación.
 */
public record UserCreated(
        UserId userId,
        Name name
) implements DomainEvent {
}