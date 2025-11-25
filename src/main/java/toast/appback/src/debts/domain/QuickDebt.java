package toast.appback.src.debts.domain;

import toast.appback.src.debts.domain.event.DebtCreated;
import toast.appback.src.debts.domain.vo.*;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.time.Instant;
import java.util.List;

public class QuickDebt extends Debt {
    private final UserId userId;
    private final Role role;
    private TargetUser targetUser;

    private QuickDebt(DebtId debtId, Context context, DebtMoney debtMoney, UserId userId, Role role, TargetUser targetUser) {
        super(debtId, context, debtMoney, Instant.now());
        this.userId = userId;
        this.role = role;
        this.targetUser = targetUser;
    }

    private QuickDebt(DebtId debtId, Context context, DebtMoney debtMoney, UserId userId, Role role, TargetUser targetUser, Status status) {
        super(debtId, context, debtMoney, Instant.now());
        this.status = status;
        this.userId = userId;
        this.role = role;
        this.targetUser = targetUser;
    }

    private QuickDebt(DebtId debtId, Context context, DebtMoney debtMoney, UserId userId, Role role, TargetUser targetUser, List<DomainEvent> debtEvents) {
        super(debtId, context, debtMoney, Instant.now(), debtEvents);
        this.userId = userId;
        this.role = role;
        this.targetUser = targetUser;
    }

    public static QuickDebt create(Context context, DebtMoney debtMoney, UserId userId, Role role, TargetUser targetUser) {
        DebtId debtId = DebtId.generate();
        QuickDebt newQuickDebt = new QuickDebt(debtId, context, debtMoney, userId, role, targetUser);
        newQuickDebt.recordEvent(new DebtCreated(newQuickDebt.getId()));
        return newQuickDebt;
    }

    public static QuickDebt load(DebtId debtId, Context context, DebtMoney debtMoney, UserId userId, Role role, TargetUser targetUser, Status status) {
        QuickDebt newQuickDebt = new QuickDebt(debtId, context, debtMoney, userId, role, targetUser, status);
        return newQuickDebt;
    }

    public void editTargetUser(TargetUser targetUser) {
        this.targetUser = targetUser;
    }

    public DebtBetweenUsers changeToDebtBetweeenUsers(UserId newUserId) {
        DebtBetweenUsers changedDebt = DebtBetweenUsers.load(super.getId(), super.getContext(), super.getDebtMoney(), this.userId, newUserId, this.status);
        return changedDebt;
    }

    @Override
    public Result<Void, DomainError> pay() {
        boolean isDebtPending = status == Status.PENDING;
        if (!isDebtPending) {
            return Result.failure(DomainError.businessRule("A debt with " + status.name() + " cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMED;
        return Result.ok();
    }

    public UserId getUserId() {
        return this.userId;
    }

    public Role getRole() {
        return this.role;
    }

    public TargetUser getTargetUser() {
        return this.targetUser;
    }

}
