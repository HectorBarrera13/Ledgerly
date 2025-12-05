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

/**
 * Agregado base que representa una deuda en el sistema.
 *
 * <p>Define el comportamiento común de todos los tipos de deuda (rápida o entre usuarios):
 * identificación, monto, contexto, estado y eventos de dominio.
 *
 * <p>Notas:
 * - Las subclases deben implementar las operaciones concretas: edición, pago y modificación del contexto.
 * - Los eventos de dominio se registran mediante {@link #recordEvent(DomainEvent)} y se recuperan con {@link #pullEvents()}.
 */
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

    /**
     * Edita el monto de la deuda respetando reglas de negocio de la implementación.
     *
     * @param debtMoney Nuevo monto a aplicar.
     * @return Resultado vacío si la operación fue válida o un {@link DomainError} con la razón.
     */
    public abstract Result<Void, DomainError> editDebtMoney(DebtMoney debtMoney);

    /**
     * Edita el contexto (propósito/descr) de la deuda.
     *
     * @param context Nuevo contexto.
     * @return Resultado vacío si la operación fue válida o un {@link DomainError} con la razón.
     */
    public abstract Result<Void, DomainError> editContext(Context context);

    /**
     * Marca la deuda como pagada o inicia el flujo de pago según la implementación.
     *
     * @return Resultado vacío si la operación fue válida o un {@link DomainError} con la razón.
     */
    public abstract Result<Void, DomainError> pay();

    /**
     * Registra un evento de dominio en el agregado.
     *
     * @param event Evento de dominio a registrar (no nulo).
     */
    public void recordEvent(DomainEvent event) {
        this.debtEvents.add(event);
    }

    /**
     * Devuelve los eventos acumulados y limpia el buffer.
     * Este patrón evita que los eventos se ejecuten múltiples veces.
     *
     * @return Lista de eventos registrados hasta el momento.
     */
    public List<DomainEvent> pullEvents() {
        List<DomainEvent> events = new ArrayList<>(debtEvents);
        debtEvents.clear();
        return events;
    }

    /**
     * @return Identificador de la deuda.
     */
    public DebtId getId() {
        return id;
    }

    /**
     * @return Contexto asociado a la deuda (propósito y descripción).
     */
    public Context getContext() {
        return context;
    }

    /**
     * @return Estado actual de la deuda.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return Monto y moneda de la deuda.
     */
    public DebtMoney getDebtMoney() {
        return debtMoney;
    }

    /**
     * @return Eventos asociados actualmente (sin limpiar).
     */
    public List<DomainEvent> getDebtEvents() {
        return debtEvents;
    }

    /**
     * @return Fecha de creación de la deuda.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }
}
