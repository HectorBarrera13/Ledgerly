package toast.appback.src.debts.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.port.DebtReadRepository;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.infrastructure.persistence.jparepository.JpaDebtRepository;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DebtReadRepositoryMySQL implements DebtReadRepository {
    private final JpaDebtRepository jpaDebtRepository;

    @Override
    public Optional<DebtView> findById(DebtId debtId) {
        return jpaDebtRepository.findDebtProjectionByDebtId(debtId.getValue())
                .map(projection -> new DebtView(
                        projection.getDebtId(),
                        projection.getPurpose(),
                        projection.getDescription(),
                        projection.getAmount(),
                        projection.getCurrency(),
                        projection.getDebtorName(),
                        projection.getCreditorName(),
                        projection.getStatus()
                ));
    }

}
