package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountFactory;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.middleware.ApplicationException;
import toast.appback.src.middleware.ErrorsHandler;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;

public class CreateAccountUseCase implements CreateAccount {
    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;

    public CreateAccountUseCase(AccountRepository accountRepository, AccountFactory accountFactory) {
        this.accountRepository = accountRepository;
        this.accountFactory = accountFactory;
    }

    @Override
    public Account execute(CreateAccountCommand command) throws ApplicationException {
        accountRepository.findByEmail(command.email())
                .ifPresent(t -> ErrorsHandler
                        .handleError(AppError.dataIntegrityViolation("Account with email already exists")));

        Result<Account, DomainError> newAccount = accountFactory.create(
                command.userId(),
                command.email(),
                command.password()
        );
        newAccount.ifFailure(ErrorsHandler::handleErrors);
        Account account = newAccount.getValue();
        accountRepository.save(account);
        return account;
    }
}
