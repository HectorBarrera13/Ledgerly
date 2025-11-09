package toast.appback.src.quickDebt.domain;

import toast.appback.src.debts.domain.Amount;
import toast.appback.src.debts.domain.Context;
import toast.appback.src.debts.domain.Status;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.users.domain.User;

import java.util.List;

import java.util.ArrayList;

public class QuickDebt {
    private final QuickDebtId id;
    private Context context;
    private Amount amount;
    private User registeredUser;
    private Role registeredUserRole;
    private String unregisteredUserName;
    private Status status;
    private List<DomainEvent> quickDebtEvents = new ArrayList<>();

    public QuickDebt(QuickDebtId id, Context context, Amount amount, User registeredUser, Role registeredUserRole, String unregisteredUserName) {
        this.id = id;
        this.context = context;
        this.amount = amount;
        this.registeredUser = registeredUser;
        this.registeredUserRole = registeredUserRole;
        this.unregisteredUserName = unregisteredUserName;
        this.status = Status.CREATED;
    }

    public QuickDebt(QuickDebtId id, Context context, Amount amount, User registeredUser, Role registeredUserRole, String unregisteredUserName, List<DomainEvent> domainEvents) {
        this(id, context, amount,  registeredUser, registeredUserRole, unregisteredUserName);
        this.quickDebtEvents.addAll(domainEvents);
    }

    public QuickDebtId getId() {
        return id;
    }

    public Context getContext() {
        return context;
    }

    public Amount getAmount() {
        return amount;
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
        boolean isDebtCreated = status.equals(Status.CREATED);
        if(!isDebtCreated){
            return Result.failure(DomainError.businessRule("Debt not created"));
        }
        return Result.success();
    }
}
