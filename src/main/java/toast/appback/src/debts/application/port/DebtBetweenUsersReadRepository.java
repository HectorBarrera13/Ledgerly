package toast.appback.src.debts.application.port;

import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebtBetweenUsersReadRepository {
    Optional<DebtBetweenUsersView> findById(DebtId debtId);
    List<DebtBetweenUsersView> getDebtsBetweenUsers(UserId userId, String role, String status, int limit);
    List<DebtBetweenUsersView> getDebtsBetweenUsersAfterCursor(UserId userId,String role, String status, UUID cursor, int limit);
}
