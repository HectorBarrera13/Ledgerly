package toast.appback.src.debts.application.communication.command;

import toast.appback.src.users.domain.UserId;

/**
 * Comando para crear una deuda rápida (QuickDebt).
 *
 * @param purpose        Propósito breve de la deuda.
 * @param description    Descripción opcional.
 * @param currency       Código ISO de moneda (3 letras).
 * @param amount         Monto en centavos (Long).
 * @param userId         Identificador del usuario que crea la deuda.
 * @param role           Rol del usuario en la deuda ("DEBTOR" o "CREDITOR").
 * @param targetUserName Nombre del usuario objetivo (no registrado) involucrado.
 */
public record CreateQuickDebtCommand(
        String purpose,
        String description,
        String currency,
        Long amount,
        UserId userId,
        String role,
        String targetUserName
) {

}
