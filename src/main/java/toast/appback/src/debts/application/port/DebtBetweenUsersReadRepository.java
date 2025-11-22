package toast.appback.src.debts.application.port;

import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

public interface DebtBetweenUsersReadRepository {
    List<DebtView> getDebtorDebtsBetweenUsers(UserId userId, int limit);
    List<DebtView> getDebtorDebtsBetweenUsersAfterCursor(UserId userId, UUID cursor, int limit);
    List<DebtView> getCreditorDebtsBetweenUsers(UserId userId, int limit);
    List<DebtView> getCreditorDebtsBetweenUsersAfterCursor(UserId userId,UUID cursor, int limit);
}
