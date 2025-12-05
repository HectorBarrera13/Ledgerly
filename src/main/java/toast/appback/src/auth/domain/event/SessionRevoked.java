package toast.appback.src.auth.domain.event;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.domain.DomainEvent;

/**
 * Evento que indica que una sesión ha sido revocada para una cuenta.
 *
 * @param accountId Identificador de la cuenta.
 * @param sessionId Identificador de la sesión revocada.
 */
public record SessionRevoked(
        AccountId accountId,
        SessionId sessionId
) implements DomainEvent {
}
