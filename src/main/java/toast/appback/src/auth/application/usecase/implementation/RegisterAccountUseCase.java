package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.AuthCommandMapper;
import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.auth.domain.*;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.EventBus;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.shared.errors.DomainError;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.auth.application.communication.result.RegisterAccountResult;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.RegisterAccount;
import toast.appback.src.users.application.usecase.contract.CreateUser;
import toast.appback.src.users.domain.User;

import java.util.Optional;

public class RegisterAccountUseCase implements RegisterAccount {

    private final AccountRepository accountRepository;

    private final AccountFactory accountFactory;

    private final TokenService tokenService;

    private final CreateUser createUserUseCase;

    private final EventBus eventBus;

    public RegisterAccountUseCase(AccountRepository accountRepository,
                                  AccountFactory accountFactory,
                                  TokenService tokenService,
                                  CreateUser createUserUseCase,
                                  EventBus eventBus) {
        this.accountRepository = accountRepository;
        this.accountFactory = accountFactory;
        this.tokenService = tokenService;
        this.createUserUseCase = createUserUseCase;
        this.eventBus = eventBus;
    }

    @Override
    public Result<RegisterAccountResult, AppError> register(RegisterAccountCommand command) {
        Optional<Account> existingAccount = accountRepository.findByEmail(command.email());
        if (existingAccount.isPresent()) {
            return Result.failure(AppError.dataIntegrityViolation("An account with the provided email already exists.")
                    .withDetails("email: " + command.email()));
        }

        CreateUserCommand createUserCommand = AuthCommandMapper.accountCommandToCreateCommand(command);
        Result<User, AppError> newUserResult = createUserUseCase.create(createUserCommand);
        if (newUserResult.isFailure()) return Result.failure(newUserResult.getErrors());

        User newUser = newUserResult.getValue();

        Result<Account, DomainError> maybeNewAccount = accountFactory.create(
                newUser.getId(),
                command.email(),
                command.password()
        );
        if (maybeNewAccount.isFailure()) {
            return Result.failure(AppError.domainError(maybeNewAccount.getErrors()));
        }
        Account account = maybeNewAccount.getValue();
        SessionId sessionId = SessionId.generate();
        Result<TokenInfo, AppError> accessTokenResult = tokenService.generateAccessToken(
                account.getAccountId().id().toString(),
                sessionId.id().toString(),
                account.getEmail().getValue()
        );
        Result<TokenInfo, AppError> refreshTokenResult = tokenService.generateRefreshToken(
                account.getAccountId().id().toString(),
                sessionId.id().toString(),
                account.getEmail().getValue()
        );
        if (accessTokenResult.isFailure()) {
            return Result.failure(accessTokenResult.getErrors());
        }
        TokenInfo accessToken = accessTokenResult.getValue();

        if (refreshTokenResult.isFailure()) {
            return Result.failure(refreshTokenResult.getErrors());
        }
        TokenInfo refreshToken = refreshTokenResult.getValue();

        Session session = Session.create(sessionId, refreshToken.token(), refreshToken.tokenType(), refreshToken.expiresAt());
        Result<Void, DomainError> result = account.addSession(session);
        if (result.isFailure()) {
            return Result.failure(AppError.domainError(result.getErrors()));
        }
        Account accountSaved = accountRepository.save(account);
        eventBus.publishAll(accountSaved.pullDomainEvents());
        return Result.success(new RegisterAccountResult(newUser, accountSaved, accessToken));
    }
}