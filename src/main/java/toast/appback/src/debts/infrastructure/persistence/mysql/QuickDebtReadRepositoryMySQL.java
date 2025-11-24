package toast.appback.src.debts.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.port.QuickDebtReadRepository;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.infrastructure.persistence.jparepository.JpaQuickDebtRepository;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class QuickDebtReadRepositoryMySQL implements QuickDebtReadRepository {
    private final JpaQuickDebtRepository jpaQuickDebtRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<QuickDebtView> findById(DebtId debtId) {
        return jpaQuickDebtRepository.findQuickDebtProjectionByDebtId(debtId.getValue())
                .map(quickDebtProjection -> new QuickDebtView(
                        quickDebtProjection.getDebtId(),
                        quickDebtProjection.getPurpose(),
                        quickDebtProjection.getDescription(),
                        quickDebtProjection.getAmount(),
                        quickDebtProjection.getCurrency(),
                        quickDebtProjection.getStatus(),
                        new UserSummaryView(
                                quickDebtProjection.getUserId(),
                                quickDebtProjection.getUserFirstName(),
                                quickDebtProjection.getUserLastName()
                        ),
                        quickDebtProjection.getRole(),
                        quickDebtProjection.getTargetUserName()
                ));
    }

    @Override
    public List<QuickDebtView> getQuickDebts(UserId userId, String role, int limit) {
        UUID userDbId = jpaUserRepository.findByUserId(userId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getUserId();

        return jpaQuickDebtRepository.getQuickDebtsProjectionByRole(userDbId, role, limit)
                .stream()
                .map(projection -> new QuickDebtView(
                        projection.getDebtId(),
                        projection.getPurpose(),
                        projection.getDescription(),
                        projection.getAmount(),
                        projection.getCurrency(),
                        projection.getStatus(),
                        new UserSummaryView(
                                projection.getUserId(),
                                projection.getUserFirstName(),
                                projection.getUserLastName()
                        ),
                        projection.getRole(),
                        projection.getTargetUserName()
                ))
                .toList();
    }

    @Override
    public List<QuickDebtView> getQuickDebtsAfterCursor(UserId userId, String role, UUID cursor, int limit) {
        return List.of();
    }


}





















