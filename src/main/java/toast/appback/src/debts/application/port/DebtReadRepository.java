package toast.appback.src.debts.application.port;

import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebtReadRepository {
    Optional<DebtView> findById(UserId userId, int limit);
    List<DebtView> getDebtorDebtsBetweenUsers(UserId userId,  int limit);
    List<DebtView> getDebtorDebtsBetweenUsersAfterCursor(UserId userId, UUID cursor, int limit);
    List<DebtView> getDebtorQuickDebts(UserId userId, int limit);
    List<DebtView> getDebtorQuickDebtsAfterCursor(UserId userId,UUID cursor, int limit);
    List<DebtView> getCreditorDebtsBetweenUsers(UserId userId, int limit);
    List<DebtView> getCreditorDebtsBetweenUsersAfterCursor(UserId userId,UUID cursor, int limit);
    List<DebtView> getCreditorQuickDebts(UserId userId, int limit);
    List<DebtView> getCreditorQuickDebtsAfterCursor(UserId userId, UUID cursor, int limit);

}
