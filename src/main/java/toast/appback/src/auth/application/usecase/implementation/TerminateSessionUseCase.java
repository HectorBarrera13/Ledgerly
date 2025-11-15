package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.exceptions.SessionNotFound;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.TerminateSession;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.application.EventBus;

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
        TokenClaims tokenClaims = tokenService.extractClaimsFromAccessTokenUnsafe(accessToken);
        Account account = accountRepository.findById(tokenClaims.accountId())
                .orElseThrow(() -> new InvalidClaimsException(String.format("Account with id %s not found", tokenClaims.accountId())));

        Session session = account.findSession(tokenClaims.sessionId())
                .orElseThrow(() -> new SessionNotFound(tokenClaims.sessionId(), account.getAccountId()));

        session.revoke();

        accountRepository.updateSessions(account);

        eventBus.publishAll(account.pullEvents());
    }
}
