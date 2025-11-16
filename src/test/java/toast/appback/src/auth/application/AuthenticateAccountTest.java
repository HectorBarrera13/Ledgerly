package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.auth.application.communication.result.Tokens;
import toast.appback.src.auth.application.exceptions.AccountNotFoundException;
import toast.appback.src.auth.application.exceptions.domain.SessionStartException;
import toast.appback.src.auth.application.mother.AccountMother;
import toast.appback.src.auth.application.mother.AccountViewMother;
import toast.appback.src.auth.application.mother.TokenMother;
import toast.appback.src.auth.application.port.AuthService;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.implementation.AuthenticateAccountUseCase;
import toast.appback.src.auth.domain.*;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.application.DomainEventBus;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.application.exceptions.UserNotFound;
import toast.appback.src.users.application.mother.UserMother;
import toast.appback.src.users.application.mother.UserViewMother;
import toast.appback.src.users.application.port.UserReadRepository;
import toast.appback.src.users.domain.User;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Authenticate account use case tests")
public class AuthenticateAccountTest {
    private AuthenticateAccountUseCase authenticateAccountUseCase;
    private final TokenService tokenService = mock(TokenService.class);
    private final AuthService authService = mock(AuthService.class);
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final UserReadRepository userReadRepository = mock(UserReadRepository.class);
    private final DomainEventBus domainEventBus = mock(DomainEventBus.class);

    private final String email = "johndoe@gmail.com";

    @BeforeEach
    public void setUp() {
        this.authenticateAccountUseCase = new AuthenticateAccountUseCase(
                tokenService,
                authService,
                accountRepository,
                userReadRepository,
                domainEventBus
        );
    }

    @Test
    @DisplayName("Should authenticate account successfully")
    public void testAuthenticateAccountSuccessfully() {
        Account account = AccountMother.withEmail(email);
        User user = UserMother.validUser();
        UserView userView = UserViewMother.create(user);
        Tokens tokens = TokenMother.create();

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        when(tokenService.generateTokens(any(), anyLong())).thenReturn(tokens);
        when(userReadRepository.findById(any())).thenReturn(Optional.of(userView));

        AuthenticateAccountCommand authenticateAccountCommand = new AuthenticateAccountCommand(
                email,
                "password123"
        );

        AuthResult authResult = authenticateAccountUseCase.execute(authenticateAccountCommand);

        assertNotNull(authResult);
        assertEquals(AccountViewMother.create(account), authResult.account());
        assertEquals(userView, authResult.user());
        assertEquals(tokens, authResult.tokens());

        verify(accountRepository, times(1)).findByEmail(email);
        verify(tokenService, times(1)).generateTokens(any(), anyLong());
        verify(userReadRepository, times(1)).findById(any());
        verify(authService, times(1)).authenticate(authenticateAccountCommand);
        verify(accountRepository, times(1)).save(account);
        verify(domainEventBus, times(1)).publishAll(anyList());

        verifyNoMoreInteractions(
                tokenService,
                authService,
                accountRepository,
                userReadRepository,
                domainEventBus
        );
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account does not exist")
    public void testAuthenticateAccountAccountNotFound() {
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        AuthenticateAccountCommand authenticateAccountCommand = new AuthenticateAccountCommand(
                email,
                "password123"
        );

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> authenticateAccountUseCase.execute(authenticateAccountCommand)
        );
        assertEquals(exception.getEmail(), email);

        verify(accountRepository, times(1)).findByEmail(email);

        verifyNoInteractions(
                tokenService,
                authService,
                userReadRepository,
                domainEventBus
        );

        verifyNoMoreInteractions(
                accountRepository
        );
    }

    @Test
    @DisplayName("Should throw SessionStartException when session limit is reached")
    public void testAuthenticateAccountSessionStartException() {
        Account account = AccountMother.withMaxSessions(email);

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        AuthenticateAccountCommand authenticateAccountCommand = new AuthenticateAccountCommand(
                email,
                "password123"
        );

        assertThrows(
                SessionStartException.class,
                () -> authenticateAccountUseCase.execute(authenticateAccountCommand)
        );

        verify(accountRepository, times(1)).findByEmail(email);
        verify(authService, times(1)).authenticate(authenticateAccountCommand);

        verifyNoInteractions(
                tokenService,
                userReadRepository,
                domainEventBus
        );

        verifyNoMoreInteractions(
                authService,
                accountRepository
        );
    }

    @Test
    @DisplayName("Should throws UserNotFound when user view does not exist")
    public void testAuthenticateAccountUserNotFound() {
        Account account = AccountMother.withEmail(email);
        Tokens tokens = TokenMother.create();

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        when(tokenService.generateTokens(any(), anyLong())).thenReturn(tokens);

        when(userReadRepository.findById(any())).thenReturn(Optional.empty());

        AuthenticateAccountCommand authenticateAccountCommand = new AuthenticateAccountCommand(
                email,
                "password123"
        );

        assertThrows(
                UserNotFound.class,
                () -> authenticateAccountUseCase.execute(authenticateAccountCommand)
        );

        verify(accountRepository, times(1)).findByEmail(email);

        verify(authService, times(1)).authenticate(authenticateAccountCommand);
        verify(tokenService, times(1)).generateTokens(any(), anyLong());
        verify(userReadRepository, times(1)).findById(any());

        verifyNoInteractions(
                domainEventBus
        );

        verifyNoMoreInteractions(
                tokenService,
                authService,
                accountRepository,
                userReadRepository
        );
    }
}
