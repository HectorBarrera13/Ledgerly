package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.exceptions.domain.InvalidateSessionException;
import toast.appback.src.auth.application.exceptions.SessionNotFound;
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
    private final AccountRepository accountRepository;
    private final EventBus eventBus;

    public TerminateSessionUseCase(TokenService tokenService,
                                   AccountRepository accountRepository,
                                   EventBus eventBus) {
        this.tokenService = tokenService;
        this.accountRepository = accountRepository;
        this.eventBus = eventBus;
    }

    @Override
    public void execute(String accessToken) {
        AccountInfo accountInfo = tokenService.extractAccountInfo(accessToken);
        Optional<Account> foundAccount = accountRepository.findById(accountInfo.accountId());
        if (foundAccount.isEmpty()) {
            throw new InvalidClaimsException(String.format("Account with id %s not found", accountInfo.accountId()));
        }
        Account account = foundAccount.get();
        Result<Void, DomainError> result = account.revokeSession(accountInfo.sessionId());
        result.ifFailureThrows(InvalidateSessionException::new);

        accountRepository.updateSessions(account);
        eventBus.publishAll(account.pullEvents());
    }
}
