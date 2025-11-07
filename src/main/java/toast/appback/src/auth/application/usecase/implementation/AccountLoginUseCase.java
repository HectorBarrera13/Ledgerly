package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.command.AccountAuthCommand;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.AccountLogin;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.EventBus;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.shared.errors.DomainError;

import java.util.Optional;

public class AccountLoginUseCase implements AccountLogin {

    private final TokenService tokenService;

    private final AuthService authService;

    private final AccountRepository accountRepository;

    private final EventBus eventBus;

    public AccountLoginUseCase(TokenService tokenService,
                               AuthService authService,
                               AccountRepository accountRepository,
                               EventBus eventBus) {
        this.tokenService = tokenService;
        this.authService = authService;
        this.accountRepository = accountRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Result<TokenInfo, AppError> login(AccountAuthCommand accountAuthCommand) {
        Optional<Account> accountOptional = accountRepository.findByEmail(accountAuthCommand.email());
        if (accountOptional.isEmpty()) {
            return Result.failure(AppError.authorizationFailed("Account not found")
                    .withDetails("email: " + accountAuthCommand.email()));
        }
        Account account = accountOptional.get();
        Result<Void, AppError> authResult = authService.authenticate(
                accountAuthCommand.email(),
                accountAuthCommand.password());
        if (authResult.isFailure()) {
            return Result.failure(authResult.getErrors());
        }

        SessionId newSessionId = SessionId.generate();
        Result<TokenInfo, AppError> accessTokenResult = tokenService.generateAccessToken(
                account.getAccountId().id().toString(),
                newSessionId.id().toString(),
                account.getEmail().getValue()
        );
        Result<TokenInfo, AppError> refreshTokenResult = tokenService.generateRefreshToken(
                account.getAccountId().id().toString(),
                newSessionId.id().toString(),
                account.getEmail().getValue()
        );
        if (accessTokenResult.isFailure()) {
            return Result.failure(accessTokenResult.getErrors());
        }
        if (refreshTokenResult.isFailure()) {
            return Result.failure(refreshTokenResult.getErrors());
        }

        TokenInfo accessToken = accessTokenResult.getValue();
        TokenInfo refreshToken = refreshTokenResult.getValue();

        Session newSession = Session.create(newSessionId, refreshToken.token(), refreshToken.tokenType(), refreshToken.expiresAt());
        Result<Void, DomainError> result = account.addSession(newSession);
        if (result.isFailure()) {
            return Result.failure(AppError.domainError(result.getErrors()));
        }
        accountRepository.updateSessions(account);
        eventBus.publishAll(account.pullDomainEvents());
        return Result.success(accessToken);
    }
}
