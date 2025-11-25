package toast.appback.src.debts.infrastructure.persistence.mapping;

import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.Status;
import toast.appback.src.debts.domain.vo.*;
import toast.appback.src.users.domain.UserId;
import toast.model.entities.debt.QuickDebtEntity;

public class QuickDebtMapper {

    public static QuickDebt toDomain(QuickDebtEntity debtEntity) {
        return QuickDebt.load(
                DebtId.load(debtEntity.getDebtId()),
                Context.load(
                        debtEntity.getPurpose(),
                        debtEntity.getDescription()
                ),
                DebtMoney.load(
                        debtEntity.getAmount(),
                        debtEntity.getCurrency()
                ),
                UserId.load(debtEntity.getUserId()),
                Role.load(debtEntity.getMyRole()),
                TargetUser.load(debtEntity.getTargetUserName()),
                Status.valueOf(debtEntity.getStatus())
        );
    }
}
