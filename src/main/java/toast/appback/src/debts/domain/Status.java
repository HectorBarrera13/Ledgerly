package toast.appback.src.debts.domain;

/**
 * Estados posibles de una deuda.
 */
public enum Status {
    PENDING,
    ACCEPTED,
    REJECTED,
    PAYMENT_CONFIRMATION_PENDING,
    PAYMENT_CONFIRMATION_REJECTED,
    PAYMENT_CONFIRMED;
}
