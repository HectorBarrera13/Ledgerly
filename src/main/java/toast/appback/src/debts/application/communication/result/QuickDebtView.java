package toast.appback.src.debts.application.communication.result;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Vista pública para una deuda rápida.
 *
 * @param debtId         Identificador público de la deuda.
 * @param purpose        Propósito breve.
 * @param description    Descripción opcional.
 * @param amount         Monto como BigDecimal.
 * @param currency       Código ISO de moneda.
 * @param status         Estado actual como cadena.
 * @param userSummary    Resumen del usuario propietario.
 * @param role           Rol del usuario en la deuda.
 * @param targetUserName Nombre del usuario objetivo.
 */
public record QuickDebtView(
        UUID debtId,
        String purpose,
        String description,
        BigDecimal amount,
        String currency,
        String status,
        UserSummaryView userSummary,
        String role,
        String targetUserName
) implements DebtView {
}
