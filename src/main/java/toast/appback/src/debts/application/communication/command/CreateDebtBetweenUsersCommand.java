package toast.appback.src.debts.application.communication.command;

import toast.appback.src.users.domain.UserId;

/**
 * Comando para crear una deuda entre dos usuarios registrados.
 *
 * @param purpose     Propósito breve de la deuda.
 * @param description Descripción opcional.
 * @param currency    Código ISO de moneda (3 letras).
 * @param amount      Monto en centavos (Long).
 * @param debtorId    Identificador del usuario deudor.
 * @param creditorId  Identificador del usuario acreedor.
 */
public record CreateDebtBetweenUsersCommand(
        String purpose,
        String description,
        String currency,
        Long amount,
        UserId debtorId,
        UserId creditorId
) {

}
