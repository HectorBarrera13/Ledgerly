package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.AcceptDebtCommand;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.shared.application.UseCaseFunction;

public interface AcceptDebt extends UseCaseFunction<Debt, AcceptDebtCommand> {
}
