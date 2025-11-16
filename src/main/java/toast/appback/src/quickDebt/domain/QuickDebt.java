package toast.appback.src.quickDebt.domain;

import toast.appback.src.debts.domain.Context;
import toast.appback.src.debts.domain.DebtMoney;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.User;

import java.util.List;

import java.util.ArrayList;

public class QuickDebt {
    private final QuickDebtId id;
    private final Context context;
    private final DebtMoney debtMoney;
    private final User registeredUser;
    private final Role registeredUserRole;
    private final String unregisteredUserName;
    private final Status status;
    private List<DomainEvent> quickDebtEvents = new ArrayList<>();

    public QuickDebt(QuickDebtId id, Context context, DebtMoney debtMoney, User registeredUser, Role registeredUserRole, String unregisteredUserName) {
        this.id = id;
        this.context = context;
        this.debtMoney = debtMoney;
        this.registeredUser = registeredUser;
        this.registeredUserRole = registeredUserRole;
        this.unregisteredUserName = unregisteredUserName;
        this.status = Status.PENDING;
    }

    public QuickDebt(QuickDebtId id, Context context, DebtMoney debtMoney, User registeredUser, Role registeredUserRole, String unregisteredUserName, List<DomainEvent> domainEvents) {
        this(id, context, debtMoney,  registeredUser, registeredUserRole, unregisteredUserName);
        this.quickDebtEvents.addAll(domainEvents);
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(quickDebtEvents);
        quickDebtEvents.clear();
        return events;
    }

    public void recordEvent(DomainEvent event) {
        quickDebtEvents.add(event);
    }

    public QuickDebtId getId() {
        return id;
    }

    public Context getContext() {
        return context;
    }

    public DebtMoney getAmount() {
        return debtMoney;
    }

    public User getRegisteredUser() {
        return registeredUser;
    }

    public Role getRegisteredUserRole() {
        return registeredUserRole;
    }

    public String getUnregisteredUserName() {
        return unregisteredUserName;
    }

    public Status getStatus() {
        return status;
    }

    public Result<Void, DomainError> pay() {
        boolean isDebtPending = status.equals(Status.PENDING);
        if(!isDebtPending){
            return Result.failure(DomainError.businessRule("Debt not pending")
                    .withBusinessCode(QuickDebtBusinessCode.DEBT_NOT_PENDING)
            );
        }
        return Result.ok();
    }
}
