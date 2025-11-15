package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.Jwt;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.exceptions.InvalidSessionException;
import toast.appback.src.auth.application.exceptions.SessionNotFound;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.RefreshSession;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;

public class RefreshSessionUseCase implements RefreshSession {
    private final TokenService tokenService;
    private final AccountRepository accountRepository;

    public RefreshSessionUseCase(TokenService tokenService,
                                 AccountRepository accountRepository) {
        this.tokenService = tokenService;
        this.accountRepository = accountRepository;
    }

    @Override
    public Jwt execute(String refreshToken) {
        TokenClaims tokenClaims = tokenService.extractClaimsFromRefreshToken(refreshToken);

        Account account = accountRepository.findById(tokenClaims.accountId())
                .orElseThrow(() -> new InvalidClaimsException(String.format("Account with id %s not found", tokenClaims.accountId())));

        SessionId sessionId = tokenClaims.sessionId();

        Session session = account.findSession(sessionId)
                .orElseThrow(() -> new SessionNotFound(sessionId, account.getAccountId()));

        if (!session.isValid()) {
            throw new InvalidSessionException(sessionId);
        }

        return tokenService.generateAccessToken(
                new TokenClaims(
                        account.getAccountId(),
                        account.getUserId(),
                        sessionId
                )
        );
    }
}
