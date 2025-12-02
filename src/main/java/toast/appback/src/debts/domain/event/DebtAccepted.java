package toast.appback.src.debts.domain.event;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.shared.domain.DomainEvent;

public record DebtAccepted(
        DebtId debtId
) implements DomainEvent {
}
