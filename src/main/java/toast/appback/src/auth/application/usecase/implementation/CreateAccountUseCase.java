package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.auth.application.exceptions.AccountExistsException;
import toast.appback.src.auth.application.exceptions.domain.CreationAccountException;
import toast.appback.src.auth.application.exceptions.domain.SessionStartException;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountFactory;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.repository.AccountRepository;

import java.util.Optional;

public class CreateAccountUseCase implements CreateAccount {
    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;

    public CreateAccountUseCase(AccountRepository accountRepository, AccountFactory accountFactory) {
        this.accountRepository = accountRepository;
        this.accountFactory = accountFactory;
    }

    @Override
    public CreateAccountResult execute(CreateAccountCommand command) {
        Optional<Account> foundAccount = accountRepository.findByEmail(command.email());
        if (foundAccount.isPresent()) {
            throw new AccountExistsException(command.email());
        }

        Account account = accountFactory.create(command)
                        .orElseThrow(CreationAccountException::new);

        Session newSession = account.startSession()
                        .orElseThrow(SessionStartException::new);

        accountRepository.save(account);

        return new CreateAccountResult(account, newSession);
    }
}
