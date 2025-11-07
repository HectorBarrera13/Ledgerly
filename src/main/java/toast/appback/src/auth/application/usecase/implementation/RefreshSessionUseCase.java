package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.RefreshSession;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.EventBus;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;

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
    public Result<TokenInfo, AppError> refresh(String refreshToken) {
        Result<AccountInfo, AppError> result = tokenService.extractClaims(refreshToken);
        if (result.isFailure()) {
            return Result.failure(result.getErrors());
        }
        AccountInfo accountInfo = result.getValue();
        Optional<Account> accountOptional = accountRepository.findBySessionId(accountInfo.sessionId().id());
        if (accountOptional.isEmpty()) {
            return Result.failure(AppError.authorizationFailed("Session not found")
                    .withDetails("sessionId: " + accountInfo.sessionId()));
        }
        Account account = accountOptional.get();
        SessionId sessionId = accountInfo.sessionId();
        if (!account.hasActiveSession(sessionId)) {
            return Result.failure(AppError.authorizationFailed("Session is not active")
                    .withDetails("sessionId: " + accountInfo.sessionId()));
        }

        Result<TokenInfo, AppError> maybeTokens = tokenService.generateAccessToken(
                account.getAccountId().id().toString(),
                sessionId.id().toString(),
                account.getEmail().getValue()
        );

        if (maybeTokens.isFailure()) {
            return Result.failure(maybeTokens.getErrors());
        }
        TokenInfo tokens = maybeTokens.getValue();

        eventBus.publishAll(account.pullDomainEvents());
        return Result.success(tokens);
    }
}
