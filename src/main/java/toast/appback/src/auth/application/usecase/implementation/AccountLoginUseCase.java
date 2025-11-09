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
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.shared.domain.DomainError;

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
    public Result<TokenInfo, AppError> execute(AccountAuthCommand command) {
        Optional<Account> foundAccount = accountRepository.findByEmail(command.email());
        if (foundAccount.isEmpty()) {
            return Result.failure(AppError.authorizationFailed("Account not found")
                    .withDetails("email: " + command.email()));
        }
        Account account = foundAccount.get();
        Result<Void, AppError> authResult = authService.authenticate(command);
        if (authResult.isFailure()) {
            return Result.failure(authResult.getErrors());
        }

        SessionId newSessionId = SessionId.generate();
        Result<TokenInfo, AppError> accessTokenResult = tokenService.generateAccessToken(
                account.getAccountId().value().toString(),
                newSessionId.value().toString(),
                account.getEmail().value()
        );

        if (accessTokenResult.isFailure()) {
            return Result.failure(accessTokenResult.getErrors());
        }

        TokenInfo accessToken = accessTokenResult.getValue();

        Session newSession = Session.create(newSessionId);
        Result<Void, DomainError> result = account.addSession(newSession);
        if (result.isFailure()) {
            return Result.failure(AppError.domainError(result.getErrors()));
        }
        accountRepository.updateSessions(account);
        eventBus.publishAll(account.pullEvents());
        return Result.success(accessToken);
    }
}
