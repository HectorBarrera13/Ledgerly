package toast.appback.src.debts.domain;

import toast.appback.src.debts.domain.vo.Context;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.domain.vo.DebtMoney;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class Debt {
    private final DebtId id;
    private final Instant createdAt;
    protected Status status = Status.PENDING;
    protected Context context;
    protected DebtMoney debtMoney;
    private List<DomainEvent> debtEvents = new ArrayList<>();


    /**
     * Constructor base del agregado.
     * Se usa en casos donde aún no hay eventos asociados.
     */
    protected Debt(DebtId id, Context context, DebtMoney debtMoney, Instant createdAt) {
        this.id = id;
        this.context = context;
        this.debtMoney = debtMoney;
        this.createdAt = createdAt;
    }

    /**
     * Constructor para reconstrucción desde persistencia, cuando ya existen eventos previos.
     */
    protected Debt(DebtId id, Context context, DebtMoney debtMoney, Instant createdAt, List<DomainEvent> debtEvents) {
        this(id, context, debtMoney, createdAt);
        this.debtEvents = debtEvents;
    }

    /**
     * Constructor alterno que permite inicializar con un estado diferente al default (PENDING).
     */
    protected Debt(DebtId id, Context context, DebtMoney debtMoney, Status status, Instant createdAt) {
        this.id = id;
        this.context = context;
        this.debtMoney = debtMoney;
        this.status = status;
        this.createdAt = createdAt;
    }

    public abstract Result<Void, DomainError> editDebtMoney(DebtMoney debtMoney);

    public abstract Result<Void, DomainError> editContext(Context context);

    public abstract Result<Void, DomainError> pay();

    public void recordEvent(DomainEvent event) {
        this.debtEvents.add(event);
    }

    /**
     * Devuelve los eventos acumulados y limpia el buffer.
     * Este patrón evita que los eventos se ejecuten múltiples veces.
     */
    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(debtEvents);
        debtEvents.clear();
        return events;
    }

    public DebtId getId() {
        return id;
    }

    public Context getContext() {
        return context;
    }

    public Status getStatus() {
        return status;
    }

    public DebtMoney getDebtMoney() {
        return debtMoney;
    }

    public List<DomainEvent> getDebtEvents() {
        return debtEvents;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

