package toast.appback.src.debts.domain.event;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.shared.domain.DomainEvent;

/**
 * Evento que indica que una deuda fue aceptada.
 *
 * @param debtId Identificador de la deuda aceptada.
 */
public record DebtAccepted(
        DebtId debtId
) implements DomainEvent {
}
