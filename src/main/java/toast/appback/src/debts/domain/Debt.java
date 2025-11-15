package toast.appback.src.debts.domain;

import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
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

    public static Result<Debt,DomainError> create(String purpose,String description, String currency, Long amount, User creditor, User debtor){
        Result<Context, DomainError> context = Context.create(purpose,description);
        Result<DebtMoney, DomainError> debtMoney = DebtMoney.create(currency, amount);
        Result<Void, DomainError> emptyResult = Result.empty();
        emptyResult.collect(context);
        emptyResult.collect(debtMoney);
        if(emptyResult.isFailure()){
            return emptyResult.castFailure();
        }
        DebtMoney validDebtMoney = debtMoney.getValue();
        Context validContext = context.getValue();
        DebtId debtId = DebtId.generate();
        Debt debt = new Debt(debtId, validContext, validDebtMoney, debtor, creditor);
        return Result.success(debt);
    }

    public DebtId getId() {return id;}

    public Context getContext() {return context;}

    public User getDebtor() {return debtor;}

    public User getCreditor() {return creditor;}

    public Status getStatus() {return status;}

    public DebtMoney getDebtMoney() {return debtMoney;}

    public List<DomainEvent> getDebtEvents() {return debtEvents;}

    public Result< Void, DomainError> accept(){
        boolean isSent =  status == Status.PENDING;
        if(!isSent){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name() +" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        this.status = Status.ACCEPTED;
        return Result.success();
    }

    public Result< Void, DomainError> reject(){
        boolean isDebtSent = status == Status.PENDING;
        if(!isDebtSent){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name() +" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        this.status = Status.REJECTED;
        return Result.success();
    }

    public Result< Void, DomainError> pay(){
        boolean isDebtAccepted = status == Status.ACCEPTED;
        if(!isDebtAccepted){
            return Result.failure(DomainError.businessRule("A debt with "+ status.name()+" cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAID;
        return Result.success();
    }

    public Result< Void, DomainError> editDebtMoney(DebtMoney debtMoney) {
        boolean isDebtSent = status == Status.PENDING;
        if (!isDebtSent) {
            return Result.failure(DomainError
                    .businessRule("A debt can only be edited if the status is 'Pending'")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        this.debtMoney = debtMoney;
        return Result.success();
    }

    public Result< Void, DomainError> editContext(Context context) {
        boolean isDebtPending = status == Status.PENDING;
        if (!isDebtPending) {
            return Result.failure(DomainError
                    .businessRule("A debt can only be edited if the status is 'Pending'")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        this.context = context;
        return Result.success();
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
