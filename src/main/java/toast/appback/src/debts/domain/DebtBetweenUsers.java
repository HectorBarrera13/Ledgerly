package toast.appback.src.debts.domain;

import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.util.List;

public class DebtBetweenUsers extends Debt {
    private UserId idDebtor;
    private UserId idCreditor;

    public DebtBetweenUsers(DebtId id, Context context, DebtMoney debtMoney, UserId idDebtor, UserId idCreditor) {
        super(id, context, debtMoney);
        this.idDebtor= idDebtor;
        this.idCreditor = idCreditor;
    }

    public DebtBetweenUsers(DebtId id, Context context, DebtMoney debtMoney, UserId idDebtor, UserId idCreditor, List<DomainEvent>  debtEvents) {
        super(id, context, debtMoney, debtEvents);
        this.idDebtor= idDebtor;
        this.idCreditor = idCreditor;
    }

    public Result< Void, DomainError> accept(){
        boolean isSent =  super.getStatus() == Status.PENDING;
        if(!isSent){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name() +" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        super.status = Status.ACCEPTED;
        return Result.ok();
    }

    public Result< Void, DomainError> reject(){
        boolean isDebtSent = status == Status.PENDING;
        if(!isDebtSent){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name() +" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        this.status = Status.REJECTED;
        return Result.ok();
    }

    public UserId getCreditorId() {
        return idCreditor;
    }

    public UserId getDebtorId() {
        return idDebtor;
    }
}
