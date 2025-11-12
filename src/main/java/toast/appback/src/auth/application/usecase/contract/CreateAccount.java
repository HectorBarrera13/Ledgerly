package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.shared.application.UseCaseFunction;

public interface CreateAccount extends UseCaseFunction<CreateAccountResult, CreateAccountCommand> {
}
