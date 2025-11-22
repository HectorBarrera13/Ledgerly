package toast.appback.src.debts.infrastructure.persistence.mysql;

import org.springframework.stereotype.Repository;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.port.QuickDebtReadRepository;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

@Repository
public class QuickDebtReadRepositoryMySQL implements QuickDebtReadRepository {
    @Override
    public List<DebtView> getDebtorQuickDebts(UserId userId, int limit) {
        return List.of();
    }

    @Override
    public List<DebtView> getDebtorQuickDebtsAfterCursor(UserId userId, UUID cursor, int limit) {
        return List.of();
    }

    @Override
    public List<DebtView> getCreditorQuickDebts(UserId userId, int limit) {
        return List.of();
    }

    @Override
    public List<DebtView> getCreditorQuickDebtsAfterCursor(UserId userId, UUID cursor, int limit) {
        return List.of();
    }
}
