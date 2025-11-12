package toast.appback.src.auth.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.RegisterAccountCommand;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.auth.application.communication.result.CreateAccountResult;
import toast.appback.src.auth.application.communication.result.RegisterAccountResult;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.contract.CreateAccount;
import toast.appback.src.auth.application.usecase.implementation.RegisterAccountUseCase;
import toast.appback.src.auth.domain.*;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.users.application.usecase.contract.CreateUser;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.domain.UserId;

import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Register account use case tests")
public class RegisterAccountTest {
    private final RegisterAccountUseCase registerAccountUseCase;
    private final CreateUser createUser = mock(CreateUser.class);
    private final CreateAccount createAccount = mock(CreateAccount.class);
    private final TokenService tokenService = mock(TokenService.class);
    private final EventBus eventBus = mock(EventBus.class);

    private final String email = "johndoe@gmail.com";

    private final AccessToken accessToken = new AccessToken(
            "accessTokenValue",
            "refreshTokenValue",
            Instant.now()
    );

    private final RegisterAccountCommand command = new RegisterAccountCommand(
            "john",
            "doe",
            "johndoe@gmail.com",
            email,
            "+1",
            "5551234567"
    );

    public RegisterAccountTest() {
        this.registerAccountUseCase = new RegisterAccountUseCase(
                createUser,
                createAccount,
                tokenService,
                eventBus
        );
    }

    /**
     * <p>Test case: Register account successfully
     * <p>Precondition: Valid registration data is provided
     * <p>Expected outcome: Account is registered successfully
     */
    @Test
    @DisplayName("Should register account successfully")
    public void testRegisterAccountSuccessfully() {
        // Mocking entities
        Account account = mock(Account.class);
        SessionId sessionId = SessionId.generate();
        User user = mock(User.class);
        CreateAccountResult createAccountResult = new CreateAccountResult(account, sessionId);

        // Mocking equals for assertion
        when(account.getAccountId()).thenReturn(AccountId.generate());
        when(user.getUserId()).thenReturn(UserId.generate());
        // Mocking session dependencies
        when(account.startSession()).thenReturn(Result.success(Session.create()));
        when(account.getEmail()).thenReturn(Email.load(email));
        // Mocking the dependencies
        when(createUser.execute(any())).thenReturn(user);
        when(createAccount.execute(any())).thenReturn(createAccountResult);
        when(tokenService.generateAccessToken(any()))
                .thenReturn(accessToken);
        // Execute the use case
        RegisterAccountResult result = registerAccountUseCase.execute(command);
        // Verify the result
        assertEquals(account, result.account());
        assertEquals(user, result.user());
        assertEquals(accessToken, result.accessToken());
        // Verify interactions
        verify(createUser, times(1)).execute(any());
        verify(createAccount, times(1)).execute(any());
        verify(tokenService, times(1)).generateAccessToken(any());
        // Events should be published for both user and account
        verify(eventBus, times(2)).publishAll(anyList());
    }

    /**
     * <p>Test case: Register account fails when account creation fails
     * <p>Precondition: Account creation fails
     * <p>Expected outcome: Exception is thrown
     */
    @Test
    @DisplayName("Should fail to register account when account creation fails")
    public void testRegisterAccountFailsWhenAccountCreationFails() {
        // Mocking entities
        User user = mock(User.class);

        // Mocking equals for assertion
        when(user.getUserId()).thenReturn(UserId.generate());
        // Mocking the dependencies
        when(createUser.execute(any())).thenReturn(user);
        when(createAccount.execute(any())).thenThrow(RuntimeException.class);
        // Execute the use case and expect an exception
        assertThrows(RuntimeException.class, () -> registerAccountUseCase.execute(command));
        // Verify interactions
        verify(createUser, times(1)).execute(any());
        verify(createAccount, times(1)).execute(any());
        verify(tokenService, never()).generateAccessToken(any());
        verify(eventBus, never()).publishAll(anyList());
    }

    /**
     * <p>Test case: Register account fails when user creation fails
     * <p>Precondition: User creation fails
     * <p>Expected outcome: Exception is thrown
     */
    @Test
    @DisplayName("Should fail to register account when user creation fails")
    public void testRegisterAccountFailsWhenUserCreationFails() {
        // Mocking the dependencies
        when(createUser.execute(any())).thenThrow(RuntimeException.class);
        // Execute the use case and expect an exception
        assertThrows(RuntimeException.class, () -> registerAccountUseCase.execute(command));
        // Verify interactions
        verify(createUser, times(1)).execute(any());
        verify(createAccount, never()).execute(any());
        verify(tokenService, never()).generateAccessToken(any());
        verify(eventBus, never()).publishAll(anyList());
    }

    /**
     * <p>Test case: Register account fails when token generation fails
     * <p>Precondition: Token generation fails
     * <p>Expected outcome: Exception is thrown
     */
    @Test
    @DisplayName("Should fail to register account when token generation fails")
    public void testRegisterAccountFailsWhenTokenGenerationFails() {
        // Mocking entities
        Account account = mock(Account.class);
        User user = mock(User.class);
        SessionId sessionId = SessionId.generate();
        CreateAccountResult createAccountResult = new CreateAccountResult(account, sessionId);
        // Mocking equals for assertion
        when(account.getAccountId()).thenReturn(AccountId.generate());
        when(user.getUserId()).thenReturn(UserId.generate());
        // Mocking session dependencies
        when(account.startSession()).thenReturn(Result.success(Session.create()));
        when(account.getEmail()).thenReturn(Email.load(email));
        // Mocking the dependencies
        when(createUser.execute(any())).thenReturn(user);
        when(createAccount.execute(any())).thenReturn(createAccountResult);
        when(tokenService.generateAccessToken(any()))
                .thenThrow(RuntimeException.class);
        // Execute the use case and expect an exception
        assertThrows(RuntimeException.class, () -> registerAccountUseCase.execute(command));
        // Verify interactions
        verify(createUser, times(1)).execute(any());
        verify(createAccount, times(1)).execute(any());
        verify(tokenService, times(1)).generateAccessToken(any());
        verify(eventBus, never()).publishAll(anyList());
    }
}
