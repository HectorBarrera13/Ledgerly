package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.auth.application.exceptions.AccountExistsException;
import toast.appback.src.auth.application.exceptions.domain.CreationAccountException;
import toast.appback.src.auth.application.exceptions.domain.SessionStartException;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.domain.*;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;

import java.util.Optional;

public class CreateAccountUseCase implements CreateAccount {
    private final AccountRepository accountRepository;
    private final PasswordHasher passwordHasher;

    public CreateAccountUseCase(
            AccountRepository accountRepository,
            PasswordHasher passwordHasher
    ) {
        this.accountRepository = accountRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public CreateAccountResult execute(CreateAccountCommand command) {
        Optional<Account> foundAccount = accountRepository.findByEmail(command.email());
        if (foundAccount.isPresent()) {
            throw new AccountExistsException(command.email());
        }

        Result<Email, DomainError> emailResult = Email.create(command.email());
        Result<Password, DomainError> passwordResult = Password.fromPlain(command.password(), passwordHasher);
        Result<Void, DomainError> creationResult = Result.empty();
        creationResult.collect(emailResult);
        creationResult.collect(passwordResult);

        creationResult.ifFailureThrows(CreationAccountException::new);

        Email validEmail = emailResult.get();
        Password validPassword = passwordResult.get();

        Account account = Account.create(
                command.userId(),
                validEmail,
                validPassword
        );

        Session newSession = account.startSession()
                        .orElseThrow(SessionStartException::new);

        accountRepository.save(account);

        return new CreateAccountResult(account, newSession);
    }
}
