package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.shared.application.UseCaseFunction;

public interface EditDebtStatus extends UseCaseFunction<DebtView, EditDebtStatusCommand> {
}
