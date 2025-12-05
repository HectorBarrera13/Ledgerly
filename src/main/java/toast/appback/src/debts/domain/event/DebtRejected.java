package toast.appback.src.debts.domain.event;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.shared.domain.DomainEvent;

/**
 * Evento que indica que una deuda fue rechazada.
 *
 * @param debtId Identificador de la deuda rechazada.
 */
public record DebtRejected(
        DebtId debtId
) implements DomainEvent {
}
