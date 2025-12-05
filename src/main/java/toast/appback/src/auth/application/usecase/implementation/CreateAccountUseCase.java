package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.auth.application.exceptions.AccountExistsException;
import toast.appback.src.auth.application.exceptions.domain.CreationAccountException;
import toast.appback.src.auth.application.exceptions.domain.SessionStartException;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Email;
import toast.appback.src.auth.domain.Password;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.auth.domain.service.PasswordHasher;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;

import java.util.Optional;

/**
 * Implementación del caso de uso que crea una cuenta para un usuario existente.
 *
 * <p>Valida que no exista una cuenta con el mismo email, valida y hashea la contraseña,
 * crea la entidad `Account` y arranca una sesión inicial.
 */
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

    /**
     * Ejecuta la creación de la cuenta.
     *
     * @param command Comando con userId, email y contraseña.
     * @return Resultado con la cuenta creada y la sesión inicial.
     * @throws AccountExistsException   Si ya existe una cuenta registrada con ese email.
     * @throws CreationAccountException Si falla la validación de email/contraseña.
     * @throws SessionStartException    Si no se pudo crear la sesión inicial.
     */
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
