package toast.appback.src.debts.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.infrastructure.persistence.jparepository.JpaDebtBetweenUsersRepository;
import toast.appback.src.debts.infrastructure.persistence.jparepository.JpaQuickDebtRepository;
import toast.appback.src.debts.infrastructure.persistence.mapping.DebtBetweenUsersMapper;
import toast.appback.src.debts.infrastructure.persistence.mapping.QuickDebtMapper;
import toast.model.entities.debt.DebtBetweenUsersEntity;
import toast.model.entities.debt.QuickDebtEntity;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DebtRepositoryMySQL implements DebtRepository {
    private final JpaDebtBetweenUsersRepository jpaDebtDebtBetweenUsersRepository;
    private final JpaQuickDebtRepository jpaQuickDebtRepository;

    @Override
    public void save(DebtBetweenUsers debtBetweenUsers) {
        DebtBetweenUsersEntity debtBetweenUsersEntity = jpaDebtDebtBetweenUsersRepository.findByDebtId(debtBetweenUsers.getId().getValue())
                .orElseGet(DebtBetweenUsersEntity::new);
        debtBetweenUsersEntity.setDebtId(debtBetweenUsers.getId().getValue());
        debtBetweenUsersEntity.setPurpose(debtBetweenUsers.getContext().getPurpose());
        debtBetweenUsersEntity.setDescription(debtBetweenUsers.getContext().getDescription());
        debtBetweenUsersEntity.setAmount(debtBetweenUsers.getDebtMoney().getAmount());
        debtBetweenUsersEntity.setCurrency(debtBetweenUsers.getDebtMoney().getCurrency());
        debtBetweenUsersEntity.setDebtorId(debtBetweenUsers.getDebtorId().getValue());
        debtBetweenUsersEntity.setCreditorId(debtBetweenUsers.getCreditorId().getValue());
        debtBetweenUsersEntity.setStatus(debtBetweenUsers.getStatus().name());
        debtBetweenUsersEntity.setCreatedAt(debtBetweenUsers.getCreatedAt());
        jpaDebtDebtBetweenUsersRepository.save(debtBetweenUsersEntity);
    }

    @Override
    public void save(QuickDebt quickDebt) {
        QuickDebtEntity quickDebtEntity = jpaQuickDebtRepository.findByDebtId(quickDebt.getId().getValue())
                .orElseGet(QuickDebtEntity::new);
        quickDebtEntity.setDebtId(quickDebt.getId().getValue());
        quickDebtEntity.setPurpose(quickDebt.getContext().getPurpose());
        quickDebtEntity.setDescription(quickDebt.getContext().getDescription());
        quickDebtEntity.setAmount(quickDebt.getDebtMoney().getAmount());
        quickDebtEntity.setCurrency(quickDebt.getDebtMoney().getCurrency());
        quickDebtEntity.setUserId(quickDebt.getUserId().getValue());
        quickDebtEntity.setMyRole(quickDebt.getRole().getValue());
        quickDebtEntity.setTargetUserName(quickDebt.getTargetUser().getName());
        quickDebtEntity.setStatus(quickDebt.getStatus().name());
        quickDebtEntity.setCreatedAt(quickDebt.getCreatedAt());
        jpaQuickDebtRepository.save(quickDebtEntity);
    }

    @Override
    public Optional<DebtBetweenUsers> findDebtBetweenUsersById(DebtId debtId) {
        return jpaDebtDebtBetweenUsersRepository.findByDebtId(debtId.getValue()).map(DebtBetweenUsersMapper::toDomain);
    }

    @Override
    public Optional<QuickDebt> findQuickDebtById(DebtId debtId) {
        return jpaQuickDebtRepository.findByDebtId(debtId.getValue()).map(QuickDebtMapper::toDomain);
    }
}
