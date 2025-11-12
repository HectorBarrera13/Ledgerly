package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.auth.application.exceptions.AccountNotFoundException;
import toast.appback.src.auth.application.exceptions.SessionStartException;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.AuthenticateAccount;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.domain.DomainError;

import java.util.Optional;

public class AuthenticateAccountUseCase implements AuthenticateAccount {
    private final TokenService tokenService;
    private final AuthService authService;
    private final AccountRepository accountRepository;
    private final EventBus eventBus;

    public AuthenticateAccountUseCase(TokenService tokenService,
                                      AuthService authService,
                                      AccountRepository accountRepository,
                                      EventBus eventBus) {
        this.tokenService = tokenService;
        this.authService = authService;
        this.accountRepository = accountRepository;
        this.eventBus = eventBus;
    }

    @Override
    public AccessToken execute(AuthenticateAccountCommand command) {
        Optional<Account> foundAccount = accountRepository.findByEmail(command.email());
        if (foundAccount.isEmpty()) {
            throw new AccountNotFoundException(command.email());
        }

        Account account = foundAccount.get();

        authService.authenticate(command);

        Result<Session, DomainError> newSessionResult = account.startSession();
        newSessionResult.ifFailureThrows(SessionStartException::new);

        Session newSession = newSessionResult.getValue();

        AccessToken accessToken = tokenService.generateAccessToken(
                account.getAccountId().getValue().toString(),
                newSession.getSessionId().getValue().toString(),
                account.getEmail().getValue()
        );

        accountRepository.updateSessions(account);

        eventBus.publishAll(account.pullEvents());

        return accessToken;
    }
}
