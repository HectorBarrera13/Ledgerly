package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.AccountLogout;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;

public class AccountLogoutUseCase implements AccountLogout {

    private final TokenService tokenService;

    private final AuthService authService;

    public AccountLogoutUseCase(TokenService tokenService, AuthService authService) {
        this.tokenService = tokenService;
        this.authService = authService;
    }

    @Override
    public Result<Void, AppError> logout(String refreshToken) {
        Result<AccountInfo, AppError> result = tokenService.extractClaims(refreshToken);
        if (result.isFailure()) {
            return Result.failure(result.getErrors());
        }
        AccountInfo accountInfo = result.getValue();
        return authService.invalidateSession(accountInfo.accountId(), accountInfo.sessionId());
    }
}
