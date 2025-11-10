package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.AccountAuthCommand;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.AccountLogin;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.middleware.ErrorsHandler;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.shared.domain.DomainError;

import java.util.Optional;

public class AccountLoginUseCase implements AccountLogin {

    private final TokenService tokenService;

    private final AuthService authService;

    private final AccountRepository accountRepository;

    private final EventBus eventBus;

    public AccountLoginUseCase(TokenService tokenService,
                               AuthService authService,
                               AccountRepository accountRepository,
                               EventBus eventBus) {
        this.tokenService = tokenService;
        this.authService = authService;
        this.accountRepository = accountRepository;
        this.eventBus = eventBus;
    }

    @Override
    public TokenInfo execute(AccountAuthCommand command) {
        Optional<Account> foundAccount = accountRepository.findByEmail(command.email());
        if (foundAccount.isEmpty()) {
            ErrorsHandler.handleError(AppError.authorizationFailed("Account not found")
                    .withDetails("email: " + command.email()));
        }

        Account account = foundAccount.get();

        Result<Void, AppError> authResult = authService.authenticate(command);
        authResult.ifFailure(ErrorsHandler::handleErrors);

        SessionId newSessionId = SessionId.generate();

        Result<TokenInfo, AppError> accessTokenResult = tokenService.generateAccessToken(
                account.getAccountId().getValue().toString(),
                newSessionId.getValue().toString(),
                account.getEmail().getValue()
        );
        accessTokenResult.ifFailure(ErrorsHandler::handleErrors);

        TokenInfo accessToken = accessTokenResult.getValue();

        Session newSession = Session.create(newSessionId);

        Result<Void, DomainError> result = account.addSession(newSession);
        result.ifFailure(ErrorsHandler::handleErrors);

        accountRepository.updateSessions(account);

        eventBus.publishAll(account.pullEvents());

        return accessToken;
    }
}
