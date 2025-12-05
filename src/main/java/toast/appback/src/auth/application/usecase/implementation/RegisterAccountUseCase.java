package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.auth.application.communication.result.Tokens;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.application.usecase.contract.RegisterAccount;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.usecase.contract.CreateUser;
import toast.appback.src.users.domain.User;

/**
 * Implementación del caso de uso que registra un nuevo usuario y crea su cuenta.
 *
 * <p>Orquesta la creación del `User`, la creación de la `Account`, la generación de tokens
 * y la publicación de eventos de dominio generados por ambos agregados.
 */
public class RegisterAccountUseCase implements RegisterAccount {
    private final CreateUser createUser;
    private final CreateAccount createAccount;
    private final TokenService tokenService;
    private final DomainEventBus domainEventBus;

    public RegisterAccountUseCase(CreateUser createUser,
                                  CreateAccount createAccount,
                                  TokenService tokenService,
                                  DomainEventBus domainEventBus) {
        this.createUser = createUser;
        this.createAccount = createAccount;
        this.tokenService = tokenService;
        this.domainEventBus = domainEventBus;
    }

    /**
     * Ejecuta el registro completo.
     *
     * @param command Comando con los datos del nuevo usuario y la cuenta.
     * @return {@link AuthResult} con la vista de cuenta, la vista del usuario y los tokens generados.
     */
    @Override
    public AuthResult execute(RegisterAccountCommand command) {
        CreateUserCommand createUserCommand = new CreateUserCommand(
                command.firstName(),
                command.lastName(),
                command.phoneCountryCode(),
                command.phoneNumber()
        );

        User newUser = createUser.execute(createUserCommand);

        CreateAccountCommand createAccountCommand =
                new CreateAccountCommand(newUser.getUserId(), command.email(), command.password());

        CreateAccountResult accountResult = createAccount.execute(createAccountCommand);

        Account newAccount = accountResult.account();

        Session session = accountResult.session();

        Tokens tokens = tokenService.generateTokens(
                new TokenClaims(
                        newAccount.getAccountId(),
                        newAccount.getUserId(),
                        session.getSessionId()
                )
        );

        UserView userView = new UserView(
                newUser.getUserId().getValue(),
                newUser.getName().getFirstName(),
                newUser.getName().getLastName(),
                newUser.getPhone().getValue()
        );

        AccountView accountView = new AccountView(
                newAccount.getAccountId().getValue(),
                newAccount.getEmail().getValue()
        );

        domainEventBus.publishAll(newUser.pullEvents());
        domainEventBus.publishAll(newAccount.pullEvents());
        return new AuthResult(
                accountView,
                userView,
                tokens
        );
    }
}
