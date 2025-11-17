package toast.appback.src.debts.domain.repository;

import toast.appback.src.debts.domain.Debt;
import toast.appback.src.debts.domain.vo.DebtId;

import java.util.Optional;

public interface DebtRepository {
    void save(Debt debt);
    Optional<Debt> findById(DebtId id);
}
