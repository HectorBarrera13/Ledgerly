package toast.appback.src.debts.domain;

import toast.appback.src.debts.domain.vo.*;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

public class QuickDebt extends Debt {
    private UserId userId;
    private Role role;
    private TargetUser targetUser;

    public QuickDebt(DebtId debtId, Context context, DebtMoney debtMoney, UserId userId, Role role, TargetUser targetUser) {
        super(debtId, context, debtMoney);
        this.userId = userId;
        this.role = role;
        this.targetUser = targetUser;
    }

    public void editTargetUser(TargetUser targetUser){
        this.targetUser = targetUser;
    }

    public DebtBetweenUsers changeToDebtBetweeenUsers(UserId newUserId){
        DebtBetweenUsers changedDebt = new DebtBetweenUsers(super.getId(),super.getContext(),super.getDebtMoney(),this.userId, newUserId);
        return changedDebt;
    }

    @Override
    public Result<Void, DomainError> reportPayment(){
        boolean isDebtPending = status == Status.PENDING;
        if(!isDebtPending){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name()+" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMED;
        return Result.ok();
    }

    @Override
    public Result<Void, DomainError> pay() {
        return null;
    }

    public UserId getUserId(){
        return this.userId;
    }
    public Role getRole(){
        return this.role;
    }
    public TargetUser getTargetUser(){
        return this.targetUser;
    }

}
