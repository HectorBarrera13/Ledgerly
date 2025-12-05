package toast.appback.src.debts.domain.event;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.shared.domain.DomainEvent;

/**
 * Evento de dominio que indica que una deuda ha sido creada.
 *
 * @param debtId Identificador de la deuda creada.
 */
public record DebtCreated(
        DebtId debtId
) implements DomainEvent {
}
