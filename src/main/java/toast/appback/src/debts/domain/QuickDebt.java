package toast.appback.src.debts.domain;

import toast.appback.src.debts.domain.event.DebtCreated;
import toast.appback.src.debts.domain.vo.*;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.domain.DomainEvent;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.time.Instant;
import java.util.List;

/**
 * Entidad de deuda rápida (QuickDebt).
 * - Involucra a un solo usuario (el propietario de la deuda).
 * - Tiene un rol (DEBTOR o CREDITOR).
 * - El "otro usuario" involucrado se representa como TargetUser (nombre).
 */
public class QuickDebt extends Debt {
    private final UserId userId;
    private final Role role;
    private TargetUser targetUser;

    /**
     * Constructor base para creación desde dominio (estado inicial PENDING).
     */
    private QuickDebt(DebtId debtId, Context context, DebtMoney debtMoney,
                      UserId userId, Role role, TargetUser targetUser) {
        super(debtId, context, debtMoney, Instant.now());
        this.userId = userId;
        this.role = role;
        this.targetUser = targetUser;
    }

    /**
     * Constructor para reconstrucción desde persistencia con estado definido.
     */
    private QuickDebt(DebtId debtId, Context context, DebtMoney debtMoney,
                      UserId userId, Role role, TargetUser targetUser, Status status) {
        super(debtId, context, debtMoney, Instant.now());
        this.status = status;
        this.userId = userId;
        this.role = role;
        this.targetUser = targetUser;
    }

    /**
     * Constructor para reconstrucción incluyendo eventos previos.
     */
    private QuickDebt(DebtId debtId, Context context, DebtMoney debtMoney,
                      UserId userId, Role role, TargetUser targetUser, List<DomainEvent> debtEvents) {
        super(debtId, context, debtMoney, Instant.now(), debtEvents);
        this.userId = userId;
        this.role = role;
        this.targetUser = targetUser;
    }

    /**
     * Factory method principal.
     * Genera un nuevo DebtId, crea la deuda en estado PENDING
     * y registra el evento DebtCreated.
     */
    public static QuickDebt create(Context context, DebtMoney debtMoney, UserId userId, Role role, TargetUser targetUser) {
        DebtId debtId = DebtId.generate();
        QuickDebt newQuickDebt = new QuickDebt(debtId, context, debtMoney, userId, role, targetUser);
        newQuickDebt.recordEvent(new DebtCreated(newQuickDebt.getId()));
        return newQuickDebt;
    }

    /**
     * Reconstrucción desde persistencia.
     */
    public static QuickDebt load(DebtId debtId, Context context, DebtMoney debtMoney,
                                 UserId userId, Role role, TargetUser targetUser, Status status) {
        return new QuickDebt(debtId, context, debtMoney, userId, role, targetUser, status);
    }

    public DebtBetweenUsers changeToDebtBetweenUsers(UserId newUserId) {
        return DebtBetweenUsers.load(
                super.getId(), super.getContext(), super.getDebtMoney(), this.userId, newUserId, this.status
        );
    }

    @Override
    public Result<Void, DomainError> pay() {
        boolean isDebtPending = status == Status.PENDING;
        if (!isDebtPending) {
            return Result.failure(
                    DomainError.businessRule("A debt with " + status.name() + " cannot be paid")
                            .withBusinessCode(DebtBusinessCode.DEBT_NO_ACCEPTED)
            );
        }
        this.status = Status.PAYMENT_CONFIRMED;
        return Result.ok();
    }

    @Override
    public Result<Void, DomainError> editDebtMoney(DebtMoney debtMoney) {
        boolean isDebtAccepted = status == Status.PENDING;
        if (!isDebtAccepted) {
            return Result.failure(
                    DomainError.businessRule("A debt can only be edited if the status is 'Pending'")
                            .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING)
            );
        }
        this.debtMoney = debtMoney;
        return Result.ok();
    }

    @Override
    public Result<Void, DomainError> editContext(Context context) {
        boolean isDebtAccepted = status == Status.PENDING;
        if (!isDebtAccepted) {
            return Result.failure(
                    DomainError.businessRule("A debt can only be edited if the status is 'Pending'")
                            .withBusinessCode(DebtBusinessCode.STATUS_NOT_PENDING)
            );
        }
        this.context = context;
        return Result.ok();
    }

    public UserId getUserId() { return this.userId; }

    public Role getRole() { return this.role; }

    public TargetUser getTargetUser() { return this.targetUser; }

}
