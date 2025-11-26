package toast.appback.src.groups.domain.event;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.domain.DomainEvent;

public record GroupDebtAdded(
        GroupId groupId,
        DebtId debtId
) implements DomainEvent {
}
