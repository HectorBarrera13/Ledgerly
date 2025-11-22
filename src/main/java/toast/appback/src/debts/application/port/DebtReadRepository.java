package toast.appback.src.debts.application.port;

import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.domain.vo.DebtId;

import java.util.Optional;

public interface DebtReadRepository {
    Optional<DebtView> findById(DebtId userId);
}
