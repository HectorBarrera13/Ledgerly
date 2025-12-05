package toast.appback.src.debts.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.debts.application.port.DebtBetweenUsersReadRepository;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.infrastructure.persistence.jparepository.JpaDebtBetweenUsersRepository;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DebtBetweenUsersReadRepositoryMySQL implements DebtBetweenUsersReadRepository {
    private final JpaDebtBetweenUsersRepository jpaDebtBetweenUsersRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<DebtBetweenUsersView> findById(DebtId debtId) {
        return jpaDebtBetweenUsersRepository.findDebtBetweenUsersProjectionByDebtId(debtId.getValue())
                .map(debtBetweenUsersProjection -> new DebtBetweenUsersView(
                        debtBetweenUsersProjection.getDebtId(),
                        debtBetweenUsersProjection.getPurpose(),
                        debtBetweenUsersProjection.getDescription(),
                        debtBetweenUsersProjection.getAmount(),
                        debtBetweenUsersProjection.getCurrency(),
                        debtBetweenUsersProjection.getStatus(),
                        new UserSummaryView(
                                debtBetweenUsersProjection.getDebtorId(),
                                debtBetweenUsersProjection.getDebtorFirstName(),
                                debtBetweenUsersProjection.getDebtorLastName()
                        ),
                        new UserSummaryView(
                                debtBetweenUsersProjection.getCreditorId(),
                                debtBetweenUsersProjection.getCreditorFirstName(),
                                debtBetweenUsersProjection.getCreditorLastName()
                        )
                ));
    }

    @Override
    public List<DebtBetweenUsersView> getDebtsBetweenUsers(UserId userId, String role, String status, int limit) {
        UUID userDbId = jpaUserRepository.findByUserId(userId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getUserId();

        return jpaDebtBetweenUsersRepository.getDebtsBetweenUsersProjectionByRole(userDbId, role, status, limit)
                .stream()
                .map(projection -> new DebtBetweenUsersView(
                        projection.getDebtId(),
                        projection.getPurpose(),
                        projection.getDescription(),
                        projection.getAmount(),
                        projection.getCurrency(),
                        projection.getStatus(),
                        new UserSummaryView(
                                projection.getDebtorId(),
                                projection.getDebtorFirstName(),
                                projection.getDebtorLastName()
                        ),
                        new UserSummaryView(
                                projection.getCreditorId(),
                                projection.getCreditorFirstName(),
                                projection.getCreditorLastName()
                        )
                ))
                .toList();
    }

    @Override
    public List<DebtBetweenUsersView> getDebtsBetweenUsersAfterCursor(UserId userId, String role, String status, UUID cursor, int limit) {

        var projections = jpaDebtBetweenUsersRepository
                .findDebtorDebtsBetweenUsersProjectionAfterCursor(userId.getValue(), role, status, cursor, limit);

        return projections.stream()
                .map(projection -> new DebtBetweenUsersView(
                        projection.getDebtId(),
                        projection.getPurpose(),
                        projection.getDescription(),
                        projection.getAmount(),
                        projection.getCurrency(),
                        projection.getStatus(),
                        new UserSummaryView(
                                projection.getDebtorId(),
                                projection.getDebtorFirstName(),
                                projection.getDebtorLastName()
                        ),
                        new UserSummaryView(
                                projection.getCreditorId(),
                                projection.getCreditorFirstName(),
                                projection.getCreditorLastName()
                        )
                ))
                .toList();
    }

}
