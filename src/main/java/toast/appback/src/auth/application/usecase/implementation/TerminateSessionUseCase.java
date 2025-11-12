package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.exceptions.InvalidateSessionException;
import toast.appback.src.auth.application.exceptions.SessionNotFound;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.TerminateSession;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;

import java.util.Optional;

public class TerminateSessionUseCase implements TerminateSession {
    private final TokenService tokenService;
    private final AuthService authService;
    private final AccountRepository accountRepository;
    private final EventBus eventBus;

    public TerminateSessionUseCase(TokenService tokenService,
                                   AuthService authService,
                                   AccountRepository accountRepository,
                                   EventBus eventBus) {
        this.tokenService = tokenService;
        this.authService = authService;
        this.accountRepository = accountRepository;
        this.eventBus = eventBus;
    }

    @Override
    public void execute(String accessToken) {
        AccountInfo accountInfo = tokenService.extractAccountInfo(accessToken);
        Optional<Account> foundAccount = accountRepository.findByAccountIdAndSessionId(
                accountInfo.accountId(),
                accountInfo.sessionId()
        );
        if (foundAccount.isEmpty()) {
            throw new SessionNotFound(accountInfo.sessionId().getValue(), accountInfo.accountId().getValue());
        }
        Account account = foundAccount.get();
        Result<Void, DomainError> result = account.revokeSession(accountInfo.sessionId());
        result.ifFailureThrows(InvalidateSessionException::new);

        accountRepository.updateSessions(account);
        eventBus.publishAll(account.pullEvents());
    }
}
