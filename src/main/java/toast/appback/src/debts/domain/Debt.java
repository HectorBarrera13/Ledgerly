package toast.appback.src.debts.domain;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.users.domain.User;

import java.util.List;


import java.util.ArrayList;

public class Debt {
    private final DebtId id;
    private Context context;
    private DebtMoney debtMoney;
    private final User debtor;
    private final User creditor;
    private Status status = Status.PENDING;
    private List<DomainEvent> debtEvents = new ArrayList<>();

    public Debt(DebtId id, Context context, DebtMoney debtMoney, User debtor, User creditor){
        this.id = id;
        this.context = context;
        this.debtor = debtor;
        this.creditor = creditor;
        this.debtMoney = debtMoney;
    }

    public Debt(DebtId id, Context context, DebtMoney debtMoney, User debtor, User creditor, List<DomainEvent> debtEvents){
        this(id, context, debtMoney, debtor, creditor);
        this.debtEvents = debtEvents;
    }


    public DebtId getId() {return id;}

    public Context getContext() {return context;}

    public User getDebtor() {return debtor;}

    public User getCreditor() {return creditor;}

    public Status getStatus() {return status;}

    public DebtMoney getAmount() {return debtMoney;}

    public List<DomainEvent> getDebtEvents() {return debtEvents;}

    public Result< Void, DomainError> accept(){
        boolean isSent =  status == Status.PENDING;
        if(!isSent){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name() +" cannot be paid"));
        }
        this.status = Status.ACCEPTED;

        return Result.success();
    }

    public Result< Void, DomainError> reject(){
        boolean isDebtSent = status == Status.PENDING;
        if(!isDebtSent){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name() +" cannot be paid"));
        }
        this.status = Status.REJECTED;
        return Result.success();
    }

    public Result< Void, DomainError> pay(){
        boolean isDebtAccepted = status == Status.ACCEPTED;
        if(!isDebtAccepted){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name()+" cannot be paid"));
        }
        this.status = Status.PAID;
        return Result.success();
    }

    public Result< Void, DomainError> editAmount(DebtMoney debtMoney) {
        boolean isDebtSent = status == Status.PENDING;
        if (!isDebtSent) {
            return Result.failure(DomainError.businessRule("A debt can only be edited if the status is 'Pending'"));
        }
        this.debtMoney = debtMoney;
        return Result.success();
    }

}
