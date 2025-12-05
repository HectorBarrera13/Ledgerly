package toast.appback.src.auth.domain.event;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.Email;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.users.domain.UserId;

/**
 * Evento de dominio que indica que se ha creado una cuenta para un usuario.
 *
 * @param accountId Identificador de la cuenta creada.
 * @param userId    Identificador del usuario asociado.
 * @param email     Email asociado a la cuenta.
 */
public record AccountCreated(
        AccountId accountId,
        UserId userId,
        Email email
) implements DomainEvent {
}