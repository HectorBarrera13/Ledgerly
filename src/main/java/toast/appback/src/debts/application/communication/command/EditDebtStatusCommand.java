package toast.appback.src.debts.application.communication.command;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

/**
 * Comando para editar el estado de una deuda (aceptar/rechazar/reenviar/confirmar pago).
 *
 * @param debtId  Identificador de la deuda.
 * @param actorId Usuario que realiza la acción (deudor o acreedor según corresponda).
 */
public record EditDebtStatusCommand(
        DebtId debtId,
        UserId actorId
) {

}
