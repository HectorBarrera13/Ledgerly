package toast.appback.src.auth.domain.event;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.domain.DomainEvent;

/**
 * Evento que indica que se ha añadido una nueva sesión a una cuenta.
 *
 * @param accountId Identificador de la cuenta.
 * @param sessionId Identificador de la sesión creada.
 */
public record SessionAdded(
        AccountId accountId,
        SessionId sessionId
) implements DomainEvent {
}
