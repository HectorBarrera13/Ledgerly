package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.auth.application.exceptions.InactiveSessionException;
import toast.appback.src.auth.application.exceptions.SessionNotFound;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.RefreshSession;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;

import java.util.Optional;

public class RefreshSessionUseCase implements RefreshSession {
    private final TokenService tokenService;
    private final AccountRepository accountRepository;

    public RefreshSessionUseCase(TokenService tokenService,
                                 AccountRepository accountRepository) {
        this.tokenService = tokenService;
        this.accountRepository = accountRepository;
    }

    @Override
    public AccessToken execute(String accessToken) {
        AccountInfo accountInfo = tokenService.extractAccountInfo(accessToken);

        Optional<Account> foundAccount = accountRepository.findBySessionId(accountInfo.sessionId());
        if (foundAccount.isEmpty()) {
            throw new SessionNotFound(accountInfo.sessionId().getValue(), accountInfo.accountId().getValue());
        }

        Account account = foundAccount.get();

        SessionId sessionId = accountInfo.sessionId();
        if (!account.hasActiveSession(sessionId)) {
            throw new InactiveSessionException(account.getAccountId().getValue());
        }

        return tokenService.generateAccessToken(
                account.getAccountId().getValue().toString(),
                sessionId.getValue().toString(),
                account.getEmail().getValue()
        );
    }
}
