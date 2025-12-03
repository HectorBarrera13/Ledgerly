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
 * Entidad de deuda entre usuarios.
 * - Tiene un deudor y un acreedor (ambos usuarios).
 */

public class DebtBetweenUsers extends Debt {
    private UserId idDebtor;
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

    public Result<Void, DomainError> accept() {
        boolean isPending = status == Status.PENDING;
        if (!isPending) {
            return Result.failure(DomainError.businessRule(buildErrorMessage("accepted"))
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        super.status = Status.ACCEPTED;
        this.recordEvent(new DebtAccepted(this.getId()));
        return Result.ok();
    }

    public Result<Void, DomainError> reject() {
        boolean isPending = status == Status.PENDING;
        if (!isPending) {
            return Result.failure(DomainError.businessRule(buildErrorMessage("rejected"))
                    .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING));
        }
        this.status = Status.REJECTED;
        this.recordEvent(new DebtRejected(this.getId()));
        return Result.ok();
    }

    public Result<Void, DomainError> resend() {
        boolean isDebtRejected = status == Status.REJECTED;
        if (!isDebtRejected) {
            return Result.failure(DomainError.businessRule(buildErrorMessage("resented"))
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PENDING;
        return Result.ok();
    }

    public Result<Void, DomainError> reportPayment() {
        boolean isDebtAccepted = status == Status.ACCEPTED;
        boolean isDebtPaymentRejected = status == Status.PAYMENT_CONFIRMATION_REJECTED;

        if (!isDebtAccepted && !isDebtPaymentRejected) {
            return Result.failure(DomainError.businessRule(buildErrorMessage("reportPayment"))
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMATION_PENDING;
        return Result.ok();
    }

    public Result<Void, DomainError> confirmPayment() {
        if (status != Status.PAYMENT_CONFIRMATION_PENDING) {
            return Result.failure(DomainError.businessRule(buildErrorMessage("confirmPayment"))
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMED;
        return Result.ok();
    }

    public Result<Void, DomainError> rejectPayment() {
        boolean isPaymentReported = status == Status.PAYMENT_CONFIRMATION_PENDING;
        if (!isPaymentReported) {
            return Result.failure(DomainError.businessRule(buildErrorMessage("rejected"))
                    .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED));
        }
        this.status = Status.PAYMENT_CONFIRMATION_REJECTED;
        return Result.ok();
    }

    @Override
    public Result<Void, DomainError> pay() {
        this.status = Status.PAYMENT_CONFIRMATION_PENDING;
        return Result.ok();
    }

    @Override
    public Result<Void, DomainError> editDebtMoney(DebtMoney debtMoney) {
        boolean isDebtEditable = status == Status.REJECTED || status == Status.PENDING;
        if (!isDebtEditable) {
            return Result.failure(
                    DomainError.businessRule(buildErrorMessage("editDebtMoney"))
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
                    DomainError.businessRule(buildErrorMessage("editContext"))
                            .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING)
            );
        }
        super.context = context;
        return Result.ok();
    }

    private String buildErrorMessage(String action) {
        return "A debt with status " + status.name() + " cannot be " + action;
    }

    public UserId getCreditorId() {
        return idCreditor;
    }

    public UserId getDebtorId() {
        return idDebtor;
    }
}
