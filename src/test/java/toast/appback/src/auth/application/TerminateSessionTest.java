package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.exceptions.domain.RevokeSessionException;
import toast.appback.src.auth.application.mother.AccountMother;
import toast.appback.src.auth.application.mother.TokenMother;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.implementation.TerminateSessionUseCase;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.application.DomainEventBus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Terminate session use case tests")
class TerminateSessionTest {
    private final TokenService tokenService = mock(TokenService.class);
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final DomainEventBus domainEventBus = mock(DomainEventBus.class);
    private TerminateSessionUseCase terminateSessionUseCase;

    @BeforeEach
    void setUp() {
        this.terminateSessionUseCase = new TerminateSessionUseCase(
                tokenService,
                accountRepository,
                domainEventBus
        );
    }

    @Test
    @DisplayName("Should terminate session successfully")
    void shouldTerminateSessionSuccessfully() {
        String accessToken = "validAccess";
        Account account = AccountMother.validAccount();
        Session session = account.startSession().get();

        TokenClaims tokenClaims = new TokenClaims(
                account.getAccountId(),
                account.getUserId(),
                session.getSessionId()
        );

        when(tokenService.extractClaimsFromAccessTokenUnsafe(accessToken)).thenReturn(tokenClaims);
        when(accountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));

        terminateSessionUseCase.execute(accessToken);

        verify(tokenService, times(1)).extractClaimsFromAccessTokenUnsafe(accessToken);
        verify(accountRepository, times(1)).findById(account.getAccountId());
        verify(accountRepository, times(1)).save(account);
        verify(domainEventBus, times(1)).publishAll(anyList());

        verifyNoMoreInteractions(
                tokenService,
                accountRepository,
                domainEventBus
        );
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    void shouldThrowExceptionWhenAccountNotFound() {
        String accessToken = "validAccess";
        TokenClaims tokenClaims = TokenMother.createClaims();

        when(tokenService.extractClaimsFromAccessTokenUnsafe(accessToken)).thenReturn(tokenClaims);
        when(accountRepository.findById(tokenClaims.accountId())).thenReturn(Optional.empty());

        assertThrows(
                InvalidClaimsException.class,
                () -> terminateSessionUseCase.execute(accessToken)
        );

        verify(tokenService, times(1)).extractClaimsFromAccessTokenUnsafe(accessToken);
        verify(accountRepository, times(1)).findById(tokenClaims.accountId());

        verifyNoInteractions(
                domainEventBus
        );

        verifyNoMoreInteractions(
                tokenService,
                accountRepository,
                domainEventBus
        );
    }

    @Test
    @DisplayName("Should throw exception RevokeSessionException when session not found")
    void shouldThrowExceptionWhenSessionRevocationFails() {
        String accessToken = "validAccess";
        Account account = AccountMother.validAccount();
        TokenClaims tokenClaims = new TokenClaims(
                account.getAccountId(),
                account.getUserId(),
                SessionId.generate()
        );

        when(tokenService.extractClaimsFromAccessTokenUnsafe(accessToken)).thenReturn(tokenClaims);
        when(accountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));

        assertThrows(
                RevokeSessionException.class,
                () -> terminateSessionUseCase.execute(accessToken)
        );

        verify(tokenService, times(1)).extractClaimsFromAccessTokenUnsafe(accessToken);
        verify(accountRepository, times(1)).findById(account.getAccountId());

        verifyNoInteractions(
                domainEventBus
        );

        verifyNoMoreInteractions(
                tokenService,
                accountRepository,
                domainEventBus
        );
    }

    @Test
    @DisplayName("Should throw exception RevokeSessionException when session already revoked")
    void shouldThrowExceptionWhenSessionAlreadyRevoked() {
        String accessToken = "validAccess";
        Account account = AccountMother.validAccount();
        Session session = account.startSession().get();
        account.revokeSession(session.getSessionId());
        TokenClaims tokenClaims = new TokenClaims(
                account.getAccountId(),
                account.getUserId(),
                session.getSessionId()
        );

        when(tokenService.extractClaimsFromAccessTokenUnsafe(accessToken)).thenReturn(tokenClaims);
        when(accountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));

        assertThrows(
                RevokeSessionException.class,
                () -> terminateSessionUseCase.execute(accessToken)
        );

        verify(tokenService, times(1)).extractClaimsFromAccessTokenUnsafe(accessToken);
        verify(accountRepository, times(1)).findById(account.getAccountId());

        verifyNoInteractions(
                domainEventBus
        );

        verifyNoMoreInteractions(
                tokenService,
                accountRepository,
                domainEventBus
        );
    }
}
