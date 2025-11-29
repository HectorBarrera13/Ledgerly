package toast.appback.src.groups.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.debts.infrastructure.persistence.jparepository.JpaDebtBetweenUsersRepository;
import toast.appback.src.groups.domain.repository.GroupDebtRepository;
import toast.appback.src.groups.domain.vo.GroupDebt;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.infrastructure.persistence.jparepository.JpaGroupDebtRepository;
import toast.appback.src.groups.infrastructure.persistence.jparepository.JpaGroupRepository;
import toast.model.entities.debt.DebtBetweenUsersEntity;
import toast.model.entities.debt.DebtEntity;
import toast.model.entities.group.GroupDebtEntity;
import toast.model.entities.group.GroupEntity;


import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupDebtRepositoryMySQL implements GroupDebtRepository {
    private final JpaDebtBetweenUsersRepository jpaDebtBetweenUsersRepository;
    private final JpaGroupRepository jpaGroupRepository;
    private final JpaGroupDebtRepository jpaGroupDebtRepository;

    @Override
    public void save(GroupDebt groupDebt) {
        DebtBetweenUsersEntity debtEntity = jpaDebtBetweenUsersRepository.findByDebtId(groupDebt.getDebtId().getValue())
                .orElseThrow(() -> new RuntimeException("Debt not found"));

        GroupEntity groupEntity = jpaGroupRepository
                .findByGroupId(groupDebt.getGroupId().getValue())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        GroupDebtEntity debt = new GroupDebtEntity();
        debt.setDebt(debtEntity);
        debt.setGroup(groupEntity);
        debt.setAddedAt(groupEntity.getCreatedAt());

        jpaGroupDebtRepository.save(debt);
    }

    @Override
    public List<GroupDebt> findByGroupId(GroupId groupId) {
        return List.of();
    }
}
