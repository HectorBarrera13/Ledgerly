package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;

import toast.appback.src.debts.domain.DebtBetweenUsers;
import toast.appback.src.shared.application.UseCaseFunction;

public interface CreateDebtBetweenUsers extends UseCaseFunction<DebtBetweenUsers, CreateDebtBetweenUsersCommand> {
}
