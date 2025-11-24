package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.shared.application.UseCaseFunction;

public interface EditDebt extends UseCaseFunction<DebtView, EditDebtCommand> {

}
