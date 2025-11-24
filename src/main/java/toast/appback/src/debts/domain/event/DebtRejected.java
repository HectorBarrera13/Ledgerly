package toast.appback.src.debts.domain.event;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.shared.domain.DomainEvent;

public record DebtRejected(
        DebtId debtId
) implements DomainEvent {
}
