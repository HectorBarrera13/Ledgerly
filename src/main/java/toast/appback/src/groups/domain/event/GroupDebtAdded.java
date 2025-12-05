package toast.appback.src.groups.domain.event;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.domain.DomainEvent;

/**
 * Evento que indica que una deuda fue asociada a un grupo.
 *
 * @param groupId Identificador del grupo.
 * @param debtId  Identificador de la deuda asociada.
 */
public record GroupDebtAdded(
        GroupId groupId,
        DebtId debtId
) implements DomainEvent {
}
