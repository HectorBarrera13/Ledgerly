package toast.appback.src.debts.domain;

import toast.appback.src.debts.domain.event.DebtAccepted;
import toast.appback.src.debts.domain.event.DebtCreated;
import toast.appback.src.debts.domain.event.DebtRejected;
import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;

import java.util.List;

public class DebtBetweenUsers extends Debt {
    private UserId idDebtor;
    private UserId idCreditor;

    private DebtBetweenUsers(DebtId id, Context context, DebtMoney debtMoney, UserId idDebtor, UserId idCreditor, String debtorName, String creditorName) {
        super(id, context, debtMoney, debtorName, creditorName);
        this.idDebtor= idDebtor;
        this.idCreditor = idCreditor;
    }

    private DebtBetweenUsers(DebtId id, Context context, DebtMoney debtMoney, UserId idDebtor, UserId idCreditor, String debtorName, String creditorName, List<DomainEvent>  debtEvents) {
        super(id, context, debtMoney, debtorName, creditorName, debtEvents);
        this.idDebtor= idDebtor;
        this.idCreditor = idCreditor;
    }

    public static DebtBetweenUsers create(Context context, DebtMoney debtMoney, UserId idDebtor, UserId idCreditor, String debtorName, String creditorName){
        DebtId debtId = DebtId.generate();
        DebtBetweenUsers newDebtBetweenUsers = new DebtBetweenUsers(debtId,context, debtMoney,idDebtor,idCreditor, debtorName, creditorName);
        newDebtBetweenUsers.recordEvent(new DebtCreated(debtId));
        return newDebtBetweenUsers;
    }

    public static  DebtBetweenUsers load(DebtId id, Context context, DebtMoney debtMoney, UserId idDebtor, UserId idCreditor, String debtorName, String creditorName){
        DebtBetweenUsers debtBetweenUsers = new DebtBetweenUsers(id, context, debtMoney, idDebtor, idCreditor, debtorName, creditorName);
        return debtBetweenUsers;
    }

    public Result< Void, DomainError> accept(){
        boolean isSent =  super.getStatus() == Status.PENDING;
        if(!isSent){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name() +" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        super.status = Status.ACCEPTED;
        this.recordEvent(new DebtAccepted(this.getId()));
        return Result.ok();
    }

    public Result< Void, DomainError> reject(){
        boolean isDebtSent = status == Status.PENDING;
        if(!isDebtSent){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name() +" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        this.status = Status.REJECTED;
        this.recordEvent(new DebtRejected(this.getId()));
        return Result.ok();
    }

    public Result<Void, DomainError> reportPayment(){
        boolean isDebtAccepted = status == Status.ACCEPTED;
        boolean isDebtPaymentRejected = status == Status.PAYMENT_CONFIRMATION_REJECTED;
        if(!isDebtAccepted && !isDebtPaymentRejected){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name()+" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMATION_PENDING;
        return Result.ok();
    }

    public Result<Void, DomainError> confirmPayment(){
        boolean isDebtAccepted = status == Status.PAYMENT_CONFIRMATION_PENDING;
        if(!isDebtAccepted){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name()+" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMED;
        return Result.ok();
    }

    public Result<Void, DomainError> rejectPayment(){
        boolean isDebtAccepted = status == Status.PAYMENT_CONFIRMATION_PENDING;
        if(!isDebtAccepted){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name()+" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMATION_REJECTED;
        return Result.ok();
    }

    @Override
    public Result<Void, DomainError> pay(){
        boolean isDebtAccepted = status == Status.ACCEPTED;
        if(!isDebtAccepted){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name() +" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMATION_PENDING;
        return Result.ok();
    }

    public UserId getCreditorId() {
        return idCreditor;
    }

    public UserId getDebtorId() {
        return idDebtor;
    }
}
