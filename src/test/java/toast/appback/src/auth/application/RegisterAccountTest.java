package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.auth.application.communication.result.Tokens;
import toast.appback.src.auth.application.mother.AccountMother;
import toast.appback.src.auth.application.mother.TokenMother;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.application.usecase.implementation.RegisterAccountUseCase;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.application.mother.UserMother;
import toast.appback.src.users.application.usecase.contract.CreateUser;
import toast.appback.src.users.domain.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DisplayName("Register account use case tests")
class RegisterAccountTest {
    private final CreateUser createUser = mock(CreateUser.class);
    private final CreateAccount createAccount = mock(CreateAccount.class);
    private final TokenService tokenService = mock(TokenService.class);
    private final DomainEventBus domainEventBus = mock(DomainEventBus.class);
    private RegisterAccountUseCase registerAccountUseCase;

    @BeforeEach
    void setUp() {
        this.registerAccountUseCase = new RegisterAccountUseCase(
                createUser,
                createAccount,
                tokenService,
                domainEventBus
        );
    }

    @Test
    @DisplayName("Should register account successfully")
    void testRegisterAccountSuccessfully() {
        User user = UserMother.validUser();
        Account account = AccountMother.withUserId(user.getUserId());
        Session session = account.startSession().get();
        Tokens tokens = TokenMother.create();
        CreateAccountResult createAccountResult = new CreateAccountResult(account, session);

        RegisterAccountCommand command = new RegisterAccountCommand(
                user.getName().getFirstName(),
                user.getName().getLastName(),
                account.getEmail().getValue(),
                account.getPassword().getHashed(),
                user.getPhone().getCountryCode(),
                user.getPhone().getNumber()
        );

        when(createUser.execute(any())).thenReturn(user);
        when(createAccount.execute(any())).thenReturn(createAccountResult);
        when(tokenService.generateTokens(any())).thenReturn(tokens);

        AuthResult result = registerAccountUseCase.execute(command);

        assertNotNull(result);
        assertEquals(tokens, result.tokens());

        verify(createUser, times(1)).execute(any());
        verify(createAccount, times(1)).execute(any());
        verify(tokenService, times(1)).generateTokens(any());
        verify(domainEventBus, times(2)).publishAll(any());

        verifyNoMoreInteractions(
                createUser,
                createAccount,
                tokenService,
                domainEventBus
        );
    }

}
