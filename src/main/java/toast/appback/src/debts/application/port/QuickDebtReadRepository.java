package toast.appback.src.debts.application.port;

import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

public interface QuickDebtReadRepository {
    List<DebtView> getDebtorQuickDebts(UserId userId, int limit);
    List<DebtView> getDebtorQuickDebtsAfterCursor(UserId userId,UUID cursor, int limit);
    List<DebtView> getCreditorQuickDebts(UserId userId, int limit);
    List<DebtView> getCreditorQuickDebtsAfterCursor(UserId userId, UUID cursor, int limit);
}
