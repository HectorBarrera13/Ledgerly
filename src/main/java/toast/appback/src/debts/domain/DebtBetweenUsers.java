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
import toast.appback.src.users.domain.UserId;

import java.time.Instant;
import java.util.List;

/**
 * Entidad concreta del agregado "Debt". Representa una deuda de usuario a usuario.
 * <p>
 * Reglas principales:
 * - Solo puede crearse en estado PENDING
 * - Puede aceptarse o rechazarse por el acreedor
 * - El pagador puede reportar un pago solo si fue aceptada previamente
 * - El acreedor debe confirmar o rechazar el pago reportado
 * - Cada transición de estado genera eventos de dominio relevantes
 * <p>
 * Esta clase extiende el comportamiento común definido en la clase abstracta Debt.
 */
public class DebtBetweenUsers extends Debt {

    /**
     * Usuario que debe pagar (deudor).
     */
    private UserId idDebtor;

    /**
     * Usuario que recibe el pago (acreedor).
     */
    private UserId idCreditor;

    /**
     * Constructor base para creación desde lógica de dominio (estado inicial PENDING, fecha now()).
     */
    private DebtBetweenUsers(DebtId id, Context context, DebtMoney debtMoney,
                             UserId idDebtor, UserId idCreditor) {
        super(id, context, debtMoney, Instant.now());
        this.idDebtor = idDebtor;
        this.idCreditor = idCreditor;
    }

    /**
     * Constructor para reconstrucción desde persistencia con un estado definido.
     */
    private DebtBetweenUsers(DebtId id, Context context, DebtMoney debtMoney,
                             UserId idDebtor, UserId idCreditor, Status status) {
        super(id, context, debtMoney, Instant.now());
        this.idDebtor = idDebtor;
        this.idCreditor = idCreditor;
        this.status = status;
    }

    /**
     * Constructor usado para reconstruir desde persistencia incluyendo eventos previos.
     */
    private DebtBetweenUsers(DebtId id, Context context, DebtMoney debtMoney,
                             UserId idDebtor, UserId idCreditor, List<DomainEvent> debtEvents) {
        super(id, context, debtMoney, Instant.now(), debtEvents);
        this.idDebtor = idDebtor;
        this.idCreditor = idCreditor;
    }

    /**
     * Factory method para creación de una deuda entre usuarios.
     * - Genera un nuevo ID
     * - Estado inicial: PENDING
     * - Registra el evento de creación
     */
    public static DebtBetweenUsers create(Context context, DebtMoney debtMoney, UserId idDebtor, UserId idCreditor) {
        DebtId debtId = DebtId.generate();
        DebtBetweenUsers newDebtBetweenUsers = new DebtBetweenUsers(debtId, context, debtMoney, idDebtor, idCreditor);

        // Evento de dominio obligatorio en creación
        newDebtBetweenUsers.recordEvent(new DebtCreated(debtId));
        return newDebtBetweenUsers;
    }

    /**
     * Reconstrucción desde persistencia.
     */
    public static DebtBetweenUsers load(DebtId id, Context context, DebtMoney debtMoney,
                                        UserId idDebtor, UserId idCreditor, Status status) {
        return new DebtBetweenUsers(id, context, debtMoney, idDebtor, idCreditor, status);
    }

    /**
     * Aceptar una deuda enviada.
     * Solo es válido si está PENDING.
     * Estado → ACCEPTED
     * Genera evento DebtAccepted
     */
    public Result<Void, DomainError> accept() {
        boolean isSent = super.getStatus() == Status.PENDING;
        if (!isSent) {
            return Result.failure(DomainError.businessRule("A debt with " + status.name() + " cannot be paid")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        super.status = Status.ACCEPTED;
        this.recordEvent(new DebtAccepted(this.getId()));
        return Result.ok();
    }

    /**
     * Rechazar una deuda enviada (PENDING).
     * Estado → REJECTED
     * Genera evento DebtRejected
     */
    public Result<Void, DomainError> reject() {
        boolean isDebtSent = status == Status.PENDING;
        if (!isDebtSent) {
            return Result.failure(DomainError.businessRule("A debt with " + status.name() + " cannot be paid")
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        this.status = Status.REJECTED;
        this.recordEvent(new DebtRejected(this.getId()));
        return Result.ok();
    }

    public Result<Void, DomainError> resend() {
        boolean isDebtRejected = status == Status.REJECTED;
        if (!isDebtRejected) {
            return Result.failure(DomainError.businessRule("A debt with " + status.name() + " cannot be resent")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PENDING;
        return Result.ok();
    }

    /**
     * Reportar un pago. Solo válido si:
     * - Estado = ACCEPTED, o
     * - Estado = PAYMENT_CONFIRMATION_REJECTED (reporte previo rechazado)
     * <p>
     * Estado → PAYMENT_CONFIRMATION_PENDING
     */
    public Result<Void, DomainError> reportPayment() {
        boolean isDebtAccepted = status == Status.ACCEPTED;
        boolean isDebtPaymentRejected = status == Status.PAYMENT_CONFIRMATION_REJECTED;

        if (!isDebtAccepted && !isDebtPaymentRejected) {
            return Result.failure(DomainError.businessRule("A debt with " + status.name() + " cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMATION_PENDING;
        return Result.ok();
    }

    /**
     * Confirmar un pago previamente reportado.
     * Solo válido si estado = PAYMENT_CONFIRMATION_PENDING
     * <p>
     * Estado → PAYMENT_CONFIRMED
     */
    public Result<Void, DomainError> confirmPayment() {
        if (status != Status.PAYMENT_CONFIRMATION_PENDING) {
            return Result.failure(DomainError.businessRule("A debt with " + status.name() + " cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMED;
        return Result.ok();
    }

    /**
     * Rechazar un pago reportado.
     * Solo válido si estado = PAYMENT_CONFIRMATION_PENDING
     * <p>
     * Estado → PAYMENT_CONFIRMATION_REJECTED
     */
    public Result<Void, DomainError> rejectPayment() {
        boolean isDebtAccepted = status == Status.PAYMENT_CONFIRMATION_PENDING;
        if (!isDebtAccepted) {
            return Result.failure(DomainError.businessRule("A debt with " + status.name() + " cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMATION_REJECTED;
        return Result.ok();
    }

    /**
     * Implementación del método abstracto pay() definido en Debt.
     * Solo se puede pagar una deuda ACCEPTED.
     * <p>
     * Estado → PAYMENT_CONFIRMATION_PENDING
     */
    @Override
    public Result<Void, DomainError> pay() {
        boolean isDebtAccepted = status == Status.ACCEPTED;
        if (!isDebtAccepted) {
            return Result.failure(DomainError.businessRule("A debt with " + status.name() + " cannot be paid")
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMATION_PENDING;
        return Result.ok();
    }

    @Override
    public Result<Void, DomainError> editDebtMoney(DebtMoney debtMoney) {
        boolean isDebtEditable = status == Status.REJECTED || status == Status.PENDING;
        if (!isDebtEditable) {
            return Result.failure(
                    DomainError.businessRule("A debt can only be edited if the status is 'Pending' and 'Rejected'")
                            .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING)
            );
        }
        super.debtMoney = debtMoney;
        return Result.ok();
    }

    @Override
    public Result<Void, DomainError> editContext(Context context) {
        boolean isDebtEditable = status == Status.REJECTED || status == Status.PENDING;
        if (!isDebtEditable) {
            return Result.failure(
                    DomainError.businessRule("A debt can only be edited if the status is 'Pending' and 'Rejected'")
                            .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING)
            );
        }
        super.context = context;
        return Result.ok();
    }

    /**
     * Getters específicos del agregado concreto.
     */
    public UserId getCreditorId() {
        return idCreditor;
    }

    public UserId getDebtorId() {
        return idDebtor;
    }
}
