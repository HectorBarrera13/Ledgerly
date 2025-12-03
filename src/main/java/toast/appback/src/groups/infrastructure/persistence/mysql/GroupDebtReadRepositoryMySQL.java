package toast.appback.src.groups.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.groups.application.communication.result.GroupDebtView;
import toast.appback.src.groups.application.port.GroupDebtReadRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.infrastructure.persistence.jparepository.JpaGroupDebtRepository;
import toast.appback.src.groups.infrastructure.persistence.jparepository.JpaGroupRepository;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.shared.application.PageResult;
import toast.appback.src.shared.infrastructure.PageMapper;
import toast.appback.src.shared.infrastructure.Pageable;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class GroupDebtReadRepositoryMySQL implements GroupDebtReadRepository {
    private final JpaGroupDebtRepository jpaGroupDebtRepository;

    @Override
    public PageResult<DebtBetweenUsersView, UUID> findUserDebtsByGroupId(GroupId groupId, UserId userId, String role, String status, PageRequest pageRequest) {
            Page<DebtBetweenUsersView> debts = jpaGroupDebtRepository.findAllDebtsByGroupIdAndUserId(
                    groupId.getValue(),
                    userId.getValue(),
                    role,
                    status,
                    PageMapper.toPageable(pageRequest)
            ).map(
                    projection -> new DebtBetweenUsersView(
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
                    )
            );
            return PageMapper.toPageResult(debts);
    }

    @Override
    public PageResult<DebtBetweenUsersView, UUID> findUserDebtsByGroupIdAfterCursor(GroupId groupId, UserId userId, String role, String status,CursorRequest<UUID> cursorRequest) {
        Page<DebtBetweenUsersView> debts = jpaGroupDebtRepository.findAllDebtsByGroupIdAndUserIdAfterCursor(
                groupId.getValue(),
                userId.getValue(),
                role,
                status,
                PageMapper.toPageable(cursorRequest)
        ).map(
                projection -> new DebtBetweenUsersView(
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
                )
        );
        return PageMapper.toPageResult(debts);
    }
}
