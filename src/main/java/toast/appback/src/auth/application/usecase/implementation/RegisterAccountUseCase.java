package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.AuthCommandMapper;
import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.auth.domain.*;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.middleware.ErrorsHandler;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.application.AppError;
import toast.appback.src.shared.domain.DomainError;
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
    public RegisterAccountResult execute(RegisterAccountCommand command) {
        Optional<Account> foundAccount = accountRepository.findByEmail(command.email());
        if (foundAccount.isPresent()) {
            ErrorsHandler.handleError(AppError.dataIntegrityViolation("an account with the provided email already exists.")
                    .withDetails("email: " + command.email()));
        }

        CreateUserCommand createUserCommand = AuthCommandMapper.accountCommandToCreateCommand(command);
        System.out.println(createUserCommand + " <- createUserCommand");
        User newUser = createUserUseCase.execute(createUserCommand);

        Result<Account, DomainError> newAccount = accountFactory.create(
                newUser.getId(),
                command.email(),
                command.password()
        );
        newAccount.ifFailure(ErrorsHandler::handleErrors);

        Account account = newAccount.getValue();

        SessionId sessionId = SessionId.generate();

        Result<TokenInfo, AppError> accessTokenResult = tokenService.generateAccessToken(
                account.getAccountId().value().toString(),
                sessionId.value().toString(),
                account.getEmail().value()
        );
        accessTokenResult.ifFailure(ErrorsHandler::handleErrors);

        TokenInfo accessToken = accessTokenResult.getValue();

        Session session = Session.create(sessionId);

        Result<Void, DomainError> result = account.addSession(session);
        result.ifFailure(ErrorsHandler::handleErrors);

        accountRepository.save(account);

        eventBus.publishAll(account.pullEvents());

        return new RegisterAccountResult(newUser, account, accessToken);
    }
}