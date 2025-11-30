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

/**
 * Clase abstracta que representa el agregado raíz "Debt" dentro del dominio.
 *
 * Esta clase define el comportamiento y las reglas de negocio compartidas entre
 * diferentes tipos concretos de deudas (por ejemplo: DebtBetweenUsers, GroupDebt, etc.).
 *
 * Responsabilidades principales:
 *  - Mantener la identidad y estado de la deuda
 *  - Exponer operaciones permitidas por el dominio (editar, pagar, registrar eventos)
 *  - Garantizar invariantes mediante validaciones internas (ej: solo editar si está PENDING)
 *
 * Es parte del dominio, por lo que debe mantenerse libre de dependencias hacia infraestructura.
 */
public abstract class Debt {
    private final DebtId id;
    private Context context;
    private DebtMoney debtMoney;
    private final Instant createdAt;
    /**
     * Estado actual de la deuda dentro de su ciclo de vida.
     * Solo debe ser modificado por reglas del dominio.
     */
    protected Status status = Status.PENDING;
    /**
     * Lista interna de Domain Events.
     * El agregado registra eventos y luego son publicados mediante pullEvents().
     */
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

    /**
     * Regla del dominio:
     *  Solo se puede editar el monto si la deuda está en estado PENDING.
     */
    public Result<Void, DomainError> editDebtMoney(DebtMoney debtMoney) {
        boolean isDebtSent = status == Status.PENDING;
        if (!isDebtSent) {
            return Result.failure(
                    DomainError.businessRule("A debt can only be edited if the status is 'Pending'")
                            .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING)
            );
        }
        this.debtMoney = debtMoney;
        return Result.ok();
    }

    /**
     * Regla del dominio:
     *  Solo se puede editar el contexto (purpose/description) si la deuda está PENDING.
     */
    public Result<Void, DomainError> editContext(Context context) {
        boolean isDebtPending = status == Status.PENDING;
        if (!isDebtPending) {
            return Result.failure(
                    DomainError.businessRule("A debt can only be edited if the status is 'Pending'")
                            .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING)
            );
        }
        this.context = context;
        return Result.ok();
    }

    /**
     * Operación abstracta definida por el dominio:
     *  Cada tipo de deuda implementa su propia forma de "pagarse".
     */
    public abstract Result<Void, DomainError> pay();

    /**
     * Registra un nuevo evento de dominio dentro del agregado.
     * No se publica aún; esto lo hace el Application Service después de pullEvents().
     */
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

    public DebtId getId() { return id; }

    public Context getContext() { return context; }

    public Status getStatus() { return status; }

    public DebtMoney getDebtMoney() { return debtMoney; }

    public List<DomainEvent> getDebtEvents() { return debtEvents; }

    public Instant getCreatedAt() { return createdAt; }
}

