package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;


/**
 * Contrato que gestiona cambios de estado para DebtBetweenUsers (aceptar/rechazar/confirmar pago/...)
 */
public interface EditDebtBetweenUsersStatus {
    DebtBetweenUsersView execute(EditDebtStatusCommand command);
}
