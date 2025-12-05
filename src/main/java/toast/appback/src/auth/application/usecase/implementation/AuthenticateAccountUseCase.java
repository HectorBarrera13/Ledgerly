package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;
import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.auth.application.communication.result.Tokens;
import toast.appback.src.auth.application.exceptions.AccountNotFoundException;
import toast.appback.src.auth.application.exceptions.domain.SessionStartException;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.AuthenticateAccount;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.application.port.UserReadRepository;

/**
 * Implementación del caso de uso que autentica una cuenta (login).
 *
 * <p>Valida que exista la cuenta, delega la validación de credenciales a {@link AuthService},
 * inicia una nueva sesión y genera tokens JWT.
 */
public class AuthenticateAccountUseCase implements AuthenticateAccount {
    private final TokenService tokenService;
    private final AuthService authService;
    private final AccountRepository accountRepository;
    private final UserReadRepository userReadRepository;
    private final DomainEventBus domainEventBus;

    public AuthenticateAccountUseCase(TokenService tokenService,
                                      AuthService authService,
                                      AccountRepository accountRepository,
                                      UserReadRepository userReadRepository,
                                      DomainEventBus domainEventBus) {
        this.tokenService = tokenService;
        this.authService = authService;
        this.accountRepository = accountRepository;
        this.userReadRepository = userReadRepository;
        this.domainEventBus = domainEventBus;
    }

    /**
     * Ejecuta el flujo de autenticación.
     *
     * @param command Comando con credenciales.
     * @return {@link AuthResult} con vistas y tokens.
     * @throws AccountNotFoundException Si no existe la cuenta.
     */
    @Override
    public AuthResult execute(AuthenticateAccountCommand command) {
        Account account = accountRepository.findByEmail(command.email())
                .orElseThrow(() -> new AccountNotFoundException(command.email()));

        authService.authenticate(command);

        Session newSession = account.startSession()
                .orElseThrow(SessionStartException::new);

        Tokens tokens = tokenService.generateTokens(
                new TokenClaims(
                        account.getAccountId(),
                        account.getUserId(),
                        newSession.getSessionId()
                )
        );


        UserView userView = userReadRepository.findById(account.getUserId())
                .orElseThrow(() -> new UserNotFound(account.getUserId()));

        AccountView accountView = new AccountView(
                account.getAccountId().getValue(),
                account.getEmail().getValue()
        );

        accountRepository.save(account);

        domainEventBus.publishAll(account.pullEvents());

        return new AuthResult(
                accountView,
                userView,
                tokens
        );
    }
}
