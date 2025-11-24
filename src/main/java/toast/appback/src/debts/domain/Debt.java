package toast.appback.src.debts.domain;

import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;


import java.util.ArrayList;

public abstract class Debt {
    private final DebtId id;
    private Context context;
    private DebtMoney debtMoney;
    protected Status status = Status.PENDING;
    private final Instant createdAt;
    private List<DomainEvent> debtEvents = new ArrayList<>();

    protected Debt(DebtId id, Context context, DebtMoney debtMoney, Instant createdAt){
        this.id = id;
        this.context = context;
        this.debtMoney = debtMoney;
        this.createdAt = createdAt;
    }

    protected Debt(DebtId id, Context context, DebtMoney debtMoney, Instant createdAt , List<DomainEvent> debtEvents){
        this(id, context, debtMoney, createdAt);
        this.debtEvents = debtEvents;
    }

    protected Debt(DebtId id, Context context, DebtMoney debtMoney, Status status, Instant createdAt){
        this.id = id;
        this.context = context;
        this.debtMoney = debtMoney;
        this.status = status;
        this.createdAt = createdAt;
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

    public abstract Result<Void, DomainError> pay();

    public void recordEvent(DomainEvent event) {
        this.debtEvents.add(event);
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(debtEvents);
        debtEvents.clear();
        return events;
    }

    public DebtId getId() {return id;}

    public Context getContext() {return context;}

    public Status getStatus() {return status;}

    public DebtMoney getDebtMoney() {return debtMoney;}

    public List<DomainEvent> getDebtEvents() {return debtEvents;}

    public Instant getCreatedAt() {return createdAt;}

}
