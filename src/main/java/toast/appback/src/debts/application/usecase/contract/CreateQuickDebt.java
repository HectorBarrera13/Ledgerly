package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.CreateQuickDebtCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.shared.application.UseCaseFunction;

public interface CreateQuickDebt extends UseCaseFunction<QuickDebtView, CreateQuickDebtCommand> {

}
