package toast.appback.src.groups.application.communication.result;

import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.model.entities.CursorIdentifiable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Vista de una deuda asociada a un grupo.
 *
 * @param DebtId          Identificador de la deuda.
 * @param purpose         Propósito de la deuda.
 * @param description     Descripción.
 * @param amount          Monto.
 * @param currency        Moneda.
 * @param status          Estado actual.
 * @param debtorSummary   Resumen del deudor.
 * @param creditorSummary Resumen del acreedor.
 */
public record GroupDebtView(
        UUID DebtId,
        String purpose,
        String description,
        BigDecimal amount,
        String currency,
        String status,
        UserSummaryView debtorSummary,
        UserSummaryView creditorSummary
) implements CursorIdentifiable<UUID> {
    @Override
    public UUID getCursorId() {
        return this.DebtId;
    }
}
