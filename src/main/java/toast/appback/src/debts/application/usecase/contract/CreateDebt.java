package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.CreateDebtCommand;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.shared.application.UseCaseFunction;

public interface CreateDebt extends UseCaseFunction<Debt, CreateDebtCommand> {
}
