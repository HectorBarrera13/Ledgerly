package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.implementation.TerminateSessionUseCase;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.Session;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Terminate session use case tests")
public class TerminateSessionTest {
    private TerminateSessionUseCase terminateSessionUseCase;
    private final TokenService tokenService = mock(TokenService.class);
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final EventBus eventBus = mock(EventBus.class);

    private final AccountId accountId = AccountId.generate();
    private final UserId userId = UserId.generate();
    private final SessionId sessionId = SessionId.generate();
    private final String email = "johndoe@gmail.com";

    @BeforeEach
    public void setUp() {
        this.terminateSessionUseCase = new TerminateSessionUseCase(
                tokenService,
                accountRepository,
                eventBus
        );
    }

    @Test
    @DisplayName("Should terminate session successfully")
    public void testTerminateSessionSuccessfully() {
        TokenClaims tokenClaims = new TokenClaims(
                accountId,
                userId,
                sessionId
        );
        Account account = mock(Account.class);
        when(account.findSession(any())).thenReturn(Optional.of(Session.create()));
        when(accountRepository.findById(any())).thenReturn(Optional.of(account));
        when(tokenService.extractClaimsFromAccessTokenUnsafe(anyString()))
                .thenReturn(tokenClaims);
        when(account.revokeSession(any()))
                .thenReturn(Result.success());
        assertDoesNotThrow(() -> terminateSessionUseCase.execute("validAccessTokenString"));

        verify(tokenService, times(1)).extractClaimsFromAccessTokenUnsafe(anyString());
        verify(accountRepository, times(1)).findById(any());
        verify(accountRepository, times(1)).updateSessions(account);
        verify(eventBus, times(1)).publishAll(account.pullEvents());
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    public void testTerminateSessionAccountNotFound() {
        TokenClaims tokenClaims = new TokenClaims(
                accountId,
                userId,
                sessionId
        );
        when(tokenService.extractClaimsFromAccessTokenUnsafe(anyString()))
                .thenReturn(tokenClaims);
        when(accountRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(InvalidClaimsException.class, () ->
            terminateSessionUseCase.execute("invalidAccessTokenString")
        );
        verify(tokenService, times(1)).extractClaimsFromAccessTokenUnsafe(anyString());
        verify(accountRepository, times(1)).findById(any());
        verifyNoInteractions(eventBus);
        verifyNoMoreInteractions(tokenService, accountRepository);
    }
}
