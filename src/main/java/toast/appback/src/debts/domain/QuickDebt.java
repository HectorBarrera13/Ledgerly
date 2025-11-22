package toast.appback.src.debts.domain;

import toast.appback.src.debts.domain.event.DebtCreated;
import toast.appback.src.debts.domain.vo.*;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;

import java.util.List;

public class   QuickDebt extends Debt {
    private final UserId userId;
    private final Role role;
    private TargetUser targetUser;

    private QuickDebt(DebtId debtId, Context context, DebtMoney debtMoney, UserId userId, Role role, TargetUser targetUser, String debtorName, String creditorName) {
        super(debtId, context, debtMoney, debtorName, creditorName);
        this.userId = userId;
        this.role = role;
        this.targetUser = targetUser;
    }

    private QuickDebt(DebtId debtId, Context context, DebtMoney debtMoney, UserId userId, Role role, TargetUser targetUser, String debtorName, String creditorName, List<DomainEvent> debtEvents) {
        super(debtId, context, debtMoney, debtorName, creditorName, debtEvents);
        this.userId = userId;
        this.role = role;
        this.targetUser = targetUser;
    }

    public static QuickDebt create(Context context, DebtMoney debtMoney, UserId userId, String userName, Role role, TargetUser targetUser) {
        DebtId debtId = DebtId.generate();
        if(role.getRole().equalsIgnoreCase("DEBTOR")){
            QuickDebt newQuickDebt = new QuickDebt(debtId, context, debtMoney, userId, role, targetUser, userName, targetUser.getName());
            newQuickDebt.recordEvent(new DebtCreated(newQuickDebt.getId()));
            return newQuickDebt;
        } else {
            QuickDebt newQuickDebt = new QuickDebt(debtId, context, debtMoney, userId, role, targetUser, targetUser.getName(), userName);
            newQuickDebt.recordEvent(new DebtCreated(newQuickDebt.getId()));
            return newQuickDebt;
        }
    }

    public static QuickDebt load(DebtId debtId, Context context, DebtMoney debtMoney, UserId userId, String userName, Role role, TargetUser targetUser) {
        if(role.getRole().equalsIgnoreCase("DEBTOR")){
            QuickDebt newQuickDebt = new QuickDebt(debtId, context, debtMoney, userId, role, targetUser, userName, targetUser.getName());
            return newQuickDebt;
        } else {
            QuickDebt newQuickDebt = new QuickDebt(debtId, context, debtMoney, userId, role, targetUser, targetUser.getName(), userName);
            return newQuickDebt;
        }
    }

    public void editTargetUser(TargetUser targetUser){
        this.targetUser = targetUser;
    }

    public DebtBetweenUsers changeToDebtBetweeenUsers(UserId newUserId){
        DebtBetweenUsers changedDebt = DebtBetweenUsers.load(super.getId(),super.getContext(),super.getDebtMoney(),this.userId, newUserId, super.getDebtorName(), super.getCreditorName());
        return changedDebt;
    }

    @Override
    public Result<Void, DomainError> pay(){
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
