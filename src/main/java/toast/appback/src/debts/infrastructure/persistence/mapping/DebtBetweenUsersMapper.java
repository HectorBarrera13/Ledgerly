package toast.appback.src.debts.infrastructure.persistence.mapping;

import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.debts.domain.Status;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.users.domain.UserId;
import toast.model.entities.debt.DebtBetweenUsersEntity;

public class DebtBetweenUsersMapper {

    public static DebtBetweenUsers toDomain(DebtBetweenUsersEntity debtEntity) {
        return DebtBetweenUsers.load(
                DebtId.load(debtEntity.getDebtId()),
                Context.load(
                        debtEntity.getPurpose(),
                        debtEntity.getDescription()
                ),
                DebtMoney.load(
                        debtEntity.getAmount(),
                        debtEntity.getCurrency()
                ),
                UserId.load(debtEntity.getDebtorId()),
                UserId.load(debtEntity.getCreditorId()),
                Status.valueOf(debtEntity.getStatus())
        );
    }
}
