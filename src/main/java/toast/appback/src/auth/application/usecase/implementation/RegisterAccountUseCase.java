package toast.appback.src.auth.application.usecase.implementation;

import toast.appback.src.auth.application.communication.AuthCommandMapper;
import toast.appback.src.auth.application.communication.command.CreateAccountCommand;
import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.auth.application.communication.result.RegisterAccountResult;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.application.usecase.contract.RegisterAccount;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.users.application.communication.command.CreateUserCommand;
import toast.appback.src.users.application.usecase.contract.CreateUser;
import toast.appback.src.users.domain.User;

public class RegisterAccountUseCase implements RegisterAccount {
    private final CreateUser createUser;
    private final CreateAccount createAccount;
    private final TokenService tokenService;
    private final EventBus eventBus;

    public RegisterAccountUseCase(CreateUser createUser,
                                  CreateAccount createAccount,
                                  TokenService tokenService,
                                  EventBus eventBus) {
        this.createUser = createUser;
        this.createAccount = createAccount;
        this.tokenService = tokenService;
        this.eventBus = eventBus;
    }

    @Override
    public RegisterAccountResult execute(RegisterAccountCommand command) {
        CreateUserCommand createUserCommand = AuthCommandMapper.accountCommandToCreateCommand(command);
        User newUser = createUser.execute(createUserCommand);

        CreateAccountCommand createAccountCommand =
                new CreateAccountCommand(newUser.getUserId(), command.email(), command.password());
        CreateAccountResult accountResult = createAccount.execute(createAccountCommand);

        Account newAccount = accountResult.account();

        SessionId sessionId = accountResult.sessionId();

        AccessToken accessToken = tokenService.generateAccessToken(
                new TokenClaims(
                        newAccount.getAccountId(),
                        sessionId,
                        newAccount.getEmail().getValue()
                )
        );

        eventBus.publishAll(newUser.pullEvents());
        eventBus.publishAll(newAccount.pullEvents());
        return new RegisterAccountResult(newUser, newAccount, accessToken);
    }
}
