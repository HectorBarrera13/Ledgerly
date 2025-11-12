package toast.appback.src.auth.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import toast.appback.src.auth.application.communication.command.AccountAuthCommand;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.shared.domain.DomainError;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpringSecurityAuthService implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final AccountRepository accountRepository;

    @Override
    public Result<Void, AppError> authenticate(AccountAuthCommand command) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            command.email(),
                            command.password()
                    )
            );
            return Result.success();
        } catch (Exception e) {
            return Result.failure(AppError.authorizationFailed("Authentication failed").withDetails(e.toString()));
        }
    }

    @Override
    public Result<Account, AppError> invalidateSession(AccountId accountId, SessionId sessionId) {
        Optional<Account> maybeAccount = accountRepository.findByAccountIdAndSessionId(accountId, sessionId);
        if (maybeAccount.isEmpty()) {
            return Result.failure(AppError.entityNotFound("Account", "Account not found")
                    .withDetails("No account found for userId: " + accountId + " and sessionId: " + sessionId));
        }
        Account account = maybeAccount.get();
        Result<Void, DomainError> revokeResult = account.revokeSession(sessionId);
        if (revokeResult.isFailure()) {
            return Result.failure(AppError.domainError(revokeResult.getErrors()));
        }
        accountRepository.updateSessions(account);
        return Result.success(account);
    }
}
