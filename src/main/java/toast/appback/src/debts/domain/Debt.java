package toast.appback.src.debts.domain;

import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;

import java.util.List;


import java.util.ArrayList;

public abstract class Debt {
    private final DebtId id;
    private Context context;
    private DebtMoney debtMoney;
    protected Status status = Status.PENDING;
    private List<DomainEvent> debtEvents = new ArrayList<>();

    protected Debt(DebtId id, Context context, DebtMoney debtMoney){
        this.id = id;
        this.context = context;
        this.debtMoney = debtMoney;
    }

    protected Debt(DebtId id, Context context, DebtMoney debtMoney, List<DomainEvent> debtEvents){
        this(id, context, debtMoney);
        this.debtEvents = debtEvents;
    }

    public DebtId getId() {return id;}

    public Context getContext() {return context;}

    public Status getStatus() {return status;}

    public DebtMoney getDebtMoney() {return debtMoney;}

    public List<DomainEvent> getDebtEvents() {return debtEvents;}

    public Result<Void, DomainError> accept(){
        boolean isDebtPending = status == Status.PENDING;
        if(!isDebtPending){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name()+" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.ACCEPTED;
        return Result.ok();
    }

    public Result<Void, DomainError> reject(){
        boolean isDebtPending = status == Status.PENDING;
        if(!isDebtPending){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name()+" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.REJECTED;
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

    public Result<Void, DomainError> rejectPayment(){
        boolean isDebtAccepted = status == Status.PAYMENT_CONFIRMATION_PENDING;
        if(!isDebtAccepted){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name()+" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMATION_REJECTED;
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

    public Result< Void, DomainError> editDebtMoney(DebtMoney debtMoney) {
        boolean isDebtSent = status == Status.PENDING;
        if (!isDebtSent) {
            return Result.failure(DomainError
                    .businessRule("A debt can only be edited if the status is 'Pending'")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        this.debtMoney = debtMoney;
        return Result.ok();
    }

    public Result< Void, DomainError> editContext(Context context) {
        boolean isDebtPending = status == Status.PENDING;
        if (!isDebtPending) {
            return Result.failure(DomainError
                    .businessRule("A debt can only be edited if the status is 'Pending'")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        this.context = context;
        return Result.ok();
    }

    public void recordEvent(DomainEvent event) {
        this.debtEvents.add(event);
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(debtEvents);
        debtEvents.clear();
        return events;
    }

}
