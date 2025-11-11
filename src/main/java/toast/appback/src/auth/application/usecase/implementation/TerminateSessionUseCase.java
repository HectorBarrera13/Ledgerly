package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.TerminateSession;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.middleware.ErrorsHandler;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.application.AppError;

public class TerminateSessionUseCase implements TerminateSession {
    private final TokenService tokenService;
    private final AuthService authService;
    private final EventBus eventBus;

    public TerminateSessionUseCase(TokenService tokenService, AuthService authService, EventBus eventBus) {
        this.tokenService = tokenService;
        this.authService = authService;
        this.eventBus = eventBus;
    }

    @Override
    public void execute(String accessToken) {
        AccountInfo accountInfo = tokenService.extractAccountInfo(accessToken);

        Result<Account, AppError> invalidateResult = authService.invalidateSession(accountInfo.accountId(), accountInfo.sessionId());
        invalidateResult.ifFailure(ErrorsHandler::handleErrors);

        Account account = invalidateResult.getValue();
        eventBus.publishAll(account.pullEvents());
    }
}
