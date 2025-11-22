package toast.appback.src.debts.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.port.DebtBetweenUsersReadRepository;
import toast.appback.src.debts.infrastructure.persistence.jparepository.JpaDebtBetweenUsersRepository;
import toast.appback.src.debts.infrastructure.persistence.jparepository.projection.DebtProjection;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DebtBetweenUsersReadRepositoryMySQL implements DebtBetweenUsersReadRepository {
    private final JpaDebtBetweenUsersRepository jpaDebtBetweenUsersRepository;

    @Override
    public List<DebtView> getDebtorDebtsBetweenUsers(UserId userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        List<DebtProjection> projections =
                jpaDebtBetweenUsersRepository.findDebtorDebtBetweenUsersProjection(userId.getValue(), pageable);
        return projections.stream()
                .map(projection -> new DebtView(
                        projection.getDebtId(),
                        projection.getPurpose(),
                        projection.getDescription(),
                        projection.getAmount(),
                        projection.getCurrency(),
                        projection.getDebtorName(),
                        projection.getCreditorName(),
                        projection.getStatus()
                ))
                .toList();
    }

    @Override
    public List<DebtView> getDebtorDebtsBetweenUsersAfterCursor(UserId userId, UUID cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit + 1, Sort.by("createdAt").descending());

        var projections = jpaDebtBetweenUsersRepository
                .findDebtorDebtsBetweenUsersProjectionAfterCursor(userId.getValue(), cursor, pageable);

        return projections.stream()
                .map(projection -> new DebtView(
                        projection.getDebtId(),
                        projection.getPurpose(),
                        projection.getDescription(),
                        projection.getAmount(),
                        projection.getCurrency(),
                        projection.getDebtorName(),
                        projection.getCreditorName(),
                        projection.getStatus()
                ))
                .toList();
    }

    @Override
    public List<DebtView> getCreditorDebtsBetweenUsers(UserId userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        List<DebtProjection> projections =
                jpaDebtBetweenUsersRepository.findCreditorDebtBetweenUsersProjection(userId.getValue(), pageable);

        return projections.stream()
                .map(projection -> new DebtView(
                        projection.getDebtId(),
                        projection.getPurpose(),
                        projection.getDescription(),
                        projection.getAmount(),
                        projection.getCurrency(),
                        projection.getDebtorName(),
                        projection.getCreditorName(),
                        projection.getStatus()
                ))
                .toList();
    }

    @Override
    public List<DebtView> getCreditorDebtsBetweenUsersAfterCursor(UserId userId, UUID cursor, int limit) {
        Pageable pageable = PageRequest.of(0, limit + 1, Sort.by("createdAt").descending());

        var projections = jpaDebtBetweenUsersRepository
                .findCreditorDebtsBetweenUsersProjectionAfterCursor(userId.getValue(), cursor, pageable);

        return projections.stream()
                .map(projection -> new DebtView(
                        projection.getDebtId(),
                        projection.getPurpose(),
                        projection.getDescription(),
                        projection.getAmount(),
                        projection.getCurrency(),
                        projection.getDebtorName(),
                        projection.getCreditorName(),
                        projection.getStatus()
                ))
                .toList();
    }
}
