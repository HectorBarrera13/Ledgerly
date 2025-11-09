package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.AccountLogout;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.shared.EventBus;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;

public class AccountLogoutUseCase implements AccountLogout {

    private final TokenService tokenService;

    private final AuthService authService;

    private final EventBus eventBus;

    public AccountLogoutUseCase(TokenService tokenService, AuthService authService, EventBus eventBus) {
        this.tokenService = tokenService;
        this.authService = authService;
        this.eventBus = eventBus;
    }

    @Override
    public Result<Void, AppError> execute(String accessToken) {
        Result<AccountInfo, AppError> claimsResult = tokenService.extractClaims(accessToken);
        if (claimsResult.isFailure()) {
            return Result.failure(claimsResult.getErrors());
        }
        AccountInfo accountInfo = claimsResult.getValue();
        Result<Account, AppError> invalidateResult = authService.invalidateSession(accountInfo.accountId(), accountInfo.sessionId());
        if (invalidateResult.isFailure()) {
            return Result.failure(invalidateResult.getErrors());
        }
        Account account = invalidateResult.getValue();
        eventBus.publishAll(account.pullEvents());
        return Result.success();
    }
}
