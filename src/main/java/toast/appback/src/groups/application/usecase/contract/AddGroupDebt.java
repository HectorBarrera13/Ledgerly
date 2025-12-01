package toast.appback.src.groups.application.usecase.contract;

import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.groups.application.communication.command.AddGroupDebtCommand;
import toast.appback.src.shared.application.UseCaseFunction;

import java.util.List;

public interface AddGroupDebt extends UseCaseFunction<List<DebtBetweenUsersView>, AddGroupDebtCommand> {
}
