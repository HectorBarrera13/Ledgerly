package toast.appback.src.debts.application.port;

import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuickDebtReadRepository {
    Optional<QuickDebtView> findById(DebtId id);
    List<QuickDebtView> getQuickDebts(UserId userId,String role, int limit);
    List<QuickDebtView> getQuickDebtsAfterCursor(UserId userId, String role, UUID cursor, int limit);
}
