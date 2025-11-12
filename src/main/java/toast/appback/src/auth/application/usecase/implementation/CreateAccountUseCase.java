package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.exceptions.AccountExistsException;
import toast.appback.src.auth.application.exceptions.CreationAccountException;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountFactory;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;

import java.util.Optional;

public class CreateAccountUseCase implements CreateAccount {
    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;

    public CreateAccountUseCase(AccountRepository accountRepository, AccountFactory accountFactory) {
        this.accountRepository = accountRepository;
        this.accountFactory = accountFactory;
    }

    @Override
    public Account execute(CreateAccountCommand command) {
        Optional<Account> foundAccount = accountRepository.findByEmail(command.email());
        if (foundAccount.isPresent()) {
            throw new AccountExistsException(command.email());
        }

        Result<Account, DomainError> newAccount = accountFactory.create(
                command.userId(),
                command.email(),
                command.password()
        );
        newAccount.ifFailureThrows(CreationAccountException::new);

        Account account = newAccount.getValue();
        accountRepository.save(account);
        return account;
    }
}
