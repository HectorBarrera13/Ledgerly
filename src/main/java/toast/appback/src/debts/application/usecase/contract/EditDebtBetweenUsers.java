package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.shared.application.UseCaseFunction;

/**
 * Contrato para editar una deuda entre usuarios.
 */
public interface EditDebtBetweenUsers extends UseCaseFunction<DebtBetweenUsersView, EditDebtCommand> {
}
