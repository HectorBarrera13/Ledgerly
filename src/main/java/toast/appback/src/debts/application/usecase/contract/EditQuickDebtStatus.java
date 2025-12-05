package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;

/**
 * Contrato que gestiona cambios de estado para QuickDebt (aceptar/rechazar/confirmar pago/...)
 */
public interface EditQuickDebtStatus {
    QuickDebtView execute(EditDebtStatusCommand command);
}
