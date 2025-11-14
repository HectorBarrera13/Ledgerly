package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.exceptions.domain.InvalidateSessionException;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.implementation.TerminateSessionUseCase;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.application.EventBus;
import toast.appback.src.shared.domain.DomainError;
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
        AccountInfo accountInfo = new AccountInfo(
                accountId,
                userId,
                sessionId,
                email
        );
        Account account = mock(Account.class);
        when(accountRepository.findById(any())).thenReturn(Optional.of(account));
        when(tokenService.extractAccountInfo(anyString()))
                .thenReturn(accountInfo);
        when(account.revokeSession(any()))
                .thenReturn(Result.success());
        assertDoesNotThrow(() -> terminateSessionUseCase.execute("validAccessTokenString"));

        verify(tokenService, times(1)).extractAccountInfo(anyString());
        verify(accountRepository, times(1)).findById(any());
        verify(accountRepository, times(1)).updateSessions(account);
        verify(eventBus, times(1)).publishAll(account.pullEvents());
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    public void testTerminateSessionAccountNotFound() {
        AccountInfo accountInfo = new AccountInfo(
                accountId,
                userId,
                sessionId,
                email
        );
        when(tokenService.extractAccountInfo(anyString()))
                .thenReturn(accountInfo);
        when(accountRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(InvalidClaimsException.class, () ->
            terminateSessionUseCase.execute("invalidAccessTokenString")
        );
        verify(tokenService, times(1)).extractAccountInfo(anyString());
        verify(accountRepository, times(1)).findById(any());
        verifyNoInteractions(eventBus);
        verifyNoMoreInteractions(tokenService, accountRepository);
    }

    @Test
    @DisplayName("Should throw exception when session revocation fails")
    public void testTerminateSessionRevocationFails() {
        AccountInfo accountInfo = new AccountInfo(
                accountId,
                userId,
                sessionId,
                email
        );
        Account account = mock(Account.class);
        when(accountRepository.findById(any())).thenReturn(Optional.of(account));
        when(tokenService.extractAccountInfo(anyString()))
                .thenReturn(accountInfo);
        when(account.revokeSession(any()))
                .thenReturn(Result.failure(DomainError.businessRule("Session revocation failed")));
        assertThrows(InvalidateSessionException.class, () ->
            terminateSessionUseCase.execute("validAccessTokenString")
        );

        verify(tokenService, times(1)).extractAccountInfo(anyString());
        verify(accountRepository, times(1)).findById(any());
        verifyNoMoreInteractions(eventBus);
    }
}
