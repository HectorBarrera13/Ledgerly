package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.RejectDebtCommand;
import toast.appback.src.debts.domain.Debt;
import toast.appback.src.shared.application.UseCaseFunction;

public interface RejectDebt extends UseCaseFunction<Debt, RejectDebtCommand> {
}
