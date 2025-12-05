package toast.appback.src.groups.application.communication.command;

import toast.appback.src.users.domain.UserId;

/**
 * Comando auxiliar que representa a un deudor en una deuda grupal.
 *
 * @param debtorId Identificador del usuario deudor.
 * @param amount   Monto asociado al deudor (en centavos).
 */
public record GroupDebtorCommand(
        UserId debtorId,
        Long amount
) {
}
