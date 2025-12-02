package toast.appback.src.debts.domain.repository;

import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.vo.DebtId;

import java.util.Optional;

public interface DebtRepository {
    void save(DebtBetweenUsers debtBetweenUsers);
    void save(QuickDebt quickDebt);
    Optional<DebtBetweenUsers> findDebtBetweenUsersById(DebtId debtId);
    Optional<QuickDebt> findQuickDebtById(DebtId debtId);
}
