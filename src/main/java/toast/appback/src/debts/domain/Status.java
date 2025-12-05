package toast.appback.src.debts.domain;

/**
 * Estados posibles de una deuda.
 * <p>
 * Descripción resumida:
 * - PENDING: Deuda creada y esperando aceptación.
 * - ACCEPTED: Deuda aceptada por el receptor.
 * - REJECTED: Deuda rechazada por el receptor.
 * - PAYMENT_CONFIRMATION_PENDING: Pago reportado y pendiente de confirmación.
 * - PAYMENT_CONFIRMATION_REJECTED: Confirmación de pago fue rechazada.
 * - PAYMENT_CONFIRMED: Pago confirmado y deuda saldada.
 */
public enum Status {
    PENDING,
    ACCEPTED,
    REJECTED,
    PAYMENT_CONFIRMATION_PENDING,
    PAYMENT_CONFIRMATION_REJECTED,
    PAYMENT_CONFIRMED
}
