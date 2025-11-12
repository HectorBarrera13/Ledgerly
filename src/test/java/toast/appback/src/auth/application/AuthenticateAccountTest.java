package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.auth.application.exceptions.AccountNotFoundException;
import toast.appback.src.auth.application.exceptions.domain.SessionStartException;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.implementation.AuthenticateAccountUseCase;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.Email;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.Result;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Authenticate account use case tests")
public class AuthenticateAccountTest {
    private AuthenticateAccountUseCase authenticateAccountUseCase;
    private final TokenService tokenService = mock(TokenService.class);
    private final AuthService authService = mock(AuthService.class);
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final EventBus eventBus = mock(EventBus.class);

    private final String email = "johndoe@gmail.com";

    @BeforeEach
    public void setUp() {
        this.authenticateAccountUseCase = new AuthenticateAccountUseCase(
                tokenService,
                authService,
                accountRepository,
                eventBus
        );
    }

    @Test
    @DisplayName("Should authenticate account successfully")
    public void testAuthenticateAccountSuccessfully() {
        Account account = mock(Account.class);
        when(account.getEmail()).thenReturn(Email.load(email));
        when(account.getAccountId()).thenReturn(AccountId.generate());
        when(accountRepository.findByEmail(any())).thenReturn(Optional.of(account));
        when(account.startSession()).thenReturn(Result.success(Session.create()));
        AccessToken accessToken = new AccessToken(
                "tokenString",
                "Bearer",
                Instant.now()
        );
        when(tokenService.generateAccessToken(any())).thenReturn(accessToken);
        AuthenticateAccountCommand command = new AuthenticateAccountCommand(
                email,
                "securePassword123"
        );
        AccessToken result = authenticateAccountUseCase.execute(command);
        assertNotNull(result);
        assertEquals(accessToken, result);
        assertEquals("tokenString", result.value());
        assertEquals("Bearer", result.type());

        verify(authService, times(1)).authenticate(command);
        verify(accountRepository, times(1)).findByEmail(any());
        verify(account, times(1)).startSession();
        verify(tokenService, times(1)).generateAccessToken(any());
        verify(accountRepository, times(1)).updateSessions(account);
        verify(eventBus, times(1)).publishAll(account.pullEvents());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account does not exist")
    public void testAuthenticateAccountAccountNotFound() {
        when(accountRepository.findByEmail(any())).thenReturn(Optional.empty());
        AuthenticateAccountCommand command = new AuthenticateAccountCommand(
                email,
                "securePassword123"
        );
        assertThrows(
                AccountNotFoundException.class,
                () -> authenticateAccountUseCase.execute(command)
        );
        verify(accountRepository, times(1)).findByEmail(any());
        verifyNoMoreInteractions(authService, tokenService, eventBus);
    }

    @Test
    @DisplayName("Should throw SessionStartException when session cannot be started")
    public void testAuthenticateAccountSessionStartFailure() {
        Account account = mock(Account.class);
        when(account.getEmail()).thenReturn(Email.load(email));
        when(accountRepository.findByEmail(any())).thenReturn(Optional.of(account));
        when(account.startSession()).thenReturn(Result.failure(
                DomainError.businessRule("Cannot start session")
        ));
        AuthenticateAccountCommand command = new AuthenticateAccountCommand(
                email,
                "securePassword123"
        );
        assertThrows(
                SessionStartException.class,
                () -> authenticateAccountUseCase.execute(command)
        );
        verify(authService, times(1)).authenticate(command);
        verify(accountRepository, times(1)).findByEmail(any());
        verify(account, times(1)).startSession();
        verifyNoMoreInteractions(tokenService, eventBus);
    }
}
