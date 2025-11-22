package toast.appback.src.debts.infrastructure.persistence.mapping;

import toast.appback.src.debts.domain.QuickDebt;
import toast.appback.src.debts.domain.vo.*;
import toast.appback.src.users.domain.UserId;
import toast.model.entities.debt.QuickDebtEntity;

public class QuickDebtMapper {
    public static QuickDebt toDomain(QuickDebtEntity debtEntity) {
        if (debtEntity == null) return null;
        return QuickDebt.load(
                DebtId.load(debtEntity.getDebtId()),
                Context.load(debtEntity.getPurpose(), debtEntity.getDescription()),
                DebtMoney.load(debtEntity.getAmount(), debtEntity.getCurrency()),
                UserId.load(debtEntity.getUserId()),
                debtEntity.getUserName(),
                Role.load(debtEntity.getRole()),
                TargetUser.load(debtEntity.getTargetUserName())
        );
    }
}
