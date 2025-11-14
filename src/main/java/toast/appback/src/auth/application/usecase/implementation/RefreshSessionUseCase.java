package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.exceptions.domain.InvalidSessionException;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.RefreshSession;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;

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

        Optional<Account> foundAccount = accountRepository.findById(accountInfo.accountId());
        if (foundAccount.isEmpty()) {
            throw new InvalidClaimsException(String.format("Account with id %s not found", accountInfo.accountId()));
        }

        Account account = foundAccount.get();

        SessionId sessionId = accountInfo.sessionId();

        Result<Void, DomainError> result = account.verifyValidSessionStatus(sessionId);
        result.ifFailureThrows(InvalidSessionException::new);

        return tokenService.generateAccessToken(
                new TokenClaims(
                        account.getAccountId(),
                        account.getUserId(),
                        sessionId,
                        account.getEmail().getValue()
                )
        );
    }
}
