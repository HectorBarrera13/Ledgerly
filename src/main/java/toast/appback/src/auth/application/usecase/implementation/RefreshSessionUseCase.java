package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.RefreshSession;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.middleware.ErrorsHandler;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.application.AppError;

import java.util.Optional;

public class RefreshSessionUseCase implements RefreshSession {

    private final TokenService tokenService;

    private final AccountRepository accountRepository;

    private final EventBus eventBus;

    public RefreshSessionUseCase(TokenService tokenService,
                                 AccountRepository accountRepository,
                                 EventBus eventBus) {
        this.tokenService = tokenService;
        this.accountRepository = accountRepository;
        this.eventBus = eventBus;
    }

    @Override
    public TokenInfo execute(String accessToken) {
        Result<AccountInfo, AppError> claimsResult = tokenService.extractClaims(accessToken);
        claimsResult.ifFailure(ErrorsHandler::handleErrors);

        AccountInfo accountInfo = claimsResult.getValue();

        Optional<Account> foundAccount = accountRepository.findBySessionId(accountInfo.sessionId());
        if (foundAccount.isEmpty()) {
            ErrorsHandler.handleError(AppError.authorizationFailed("Session not found")
                    .withDetails("sessionId: " + accountInfo.sessionId()));
        }

        Account account = foundAccount.get();

        SessionId sessionId = accountInfo.sessionId();
        if (!account.hasActiveSession(sessionId)) {
            ErrorsHandler.handleError(AppError.authorizationFailed("Session is not active")
                    .withDetails("sessionId: " + accountInfo.sessionId()));
        }

        Result<TokenInfo, AppError> accessTokenResult = tokenService.generateAccessToken(
                account.getAccountId().value().toString(),
                sessionId.value().toString(),
                account.getEmail().value()
        );
        accessTokenResult.ifFailure(ErrorsHandler::handleErrors);

        TokenInfo accessTokenRefreshed = accessTokenResult.getValue();

        eventBus.publishAll(account.pullEvents());

        return accessTokenRefreshed;
    }
}
