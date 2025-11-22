package toast.appback.src.debts.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.infrastructure.persistence.jparepository.JpaDebtBetweenUsersRepository;
import toast.appback.src.debts.infrastructure.persistence.jparepository.JpaDebtRepository;
import toast.appback.src.debts.infrastructure.persistence.jparepository.JpaQuickDebtRepository;
import toast.model.entities.debt.DebtBetweenUsersEntity;
import toast.model.entities.debt.DebtEntity;
import toast.model.entities.debt.QuickDebtEntity;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DebtRepositoryMySQL implements DebtRepository {
    private final JpaDebtBetweenUsersRepository jpaDebtDebtBetweenUsersRepository;
    private final JpaQuickDebtRepository jpaQuickDebtRepository;

    @Override
    public void save(Debt debt) {

    }

    @Override
    public void save(DebtBetweenUsers debtBetweenUsers) {
        DebtBetweenUsersEntity debtBetweenUsersEntity = jpaDebtDebtBetweenUsersRepository.findByDebtId(debtBetweenUsers.getId().getValue())
                .orElseGet(DebtBetweenUsersEntity::new);
        debtBetweenUsersEntity.setDebtId(debtBetweenUsers.getId().getValue());
        debtBetweenUsersEntity.setPurpose(debtBetweenUsers.getContext().getPurpose());
        debtBetweenUsersEntity.setDescription(debtBetweenUsers.getContext().getDescription());
        debtBetweenUsersEntity.setAmount(debtBetweenUsers.getDebtMoney().getAmount().longValue());
        debtBetweenUsersEntity.setCurrency(debtBetweenUsers.getDebtMoney().getCurrency());
        debtBetweenUsersEntity.setDebtorId(debtBetweenUsers.getDebtorId().getValue());
        debtBetweenUsersEntity.setCreditorId(debtBetweenUsers.getCreditorId().getValue());
        debtBetweenUsersEntity.setStatus(debtBetweenUsers.getStatus().name());
        jpaDebtDebtBetweenUsersRepository.save(debtBetweenUsersEntity);
    }

    @Override
    public void save(QuickDebt quickDebt) {

    }

    @Override
    public Optional<Debt> findDebtById(DebtId id) {
        return Optional.empty();
    }

    @Override
    public Optional<DebtBetweenUsers> findDebtBetweenUsersById(DebtId debtId) {
        return Optional.empty();
    }

    @Override
    public Optional<QuickDebt> findQuickDebtById(DebtId debtId) {
        return Optional.empty();
    }
}
