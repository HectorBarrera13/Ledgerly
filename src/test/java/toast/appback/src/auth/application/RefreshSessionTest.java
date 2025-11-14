package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.exceptions.domain.InvalidSessionException;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.implementation.RefreshSessionUseCase;
import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.Email;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.domain.repository.AccountRepository;
import toast.appback.src.shared.domain.DomainError;
import toast.appback.src.shared.utils.result.Result;
import toast.appback.src.users.domain.UserId;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Refresh session use case tests")
public class RefreshSessionTest {
    private RefreshSessionUseCase refreshSessionUseCase;
    private final TokenService tokenService = mock(TokenService.class);
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final AccountId accountId = AccountId.generate();
    private final UserId userId = UserId.generate();
    private final SessionId sessionId = SessionId.generate();
    private final String email = "johndoe@gmail.com";

    @BeforeEach
    public void setUp() {
        this.refreshSessionUseCase = new RefreshSessionUseCase(
                tokenService,
                accountRepository
        );
    }

    @Test
    @DisplayName("Should refresh session successfully")
    public void testRefreshSessionSuccessfully() {
        AccountInfo accountInfo = new AccountInfo(
                accountId,
                userId,
                sessionId,
                email
        );
        when(tokenService.extractAccountInfo(anyString()))
                .thenReturn(accountInfo);
        Account account = mock(Account.class);
        when(account.getEmail()).thenReturn(Email.load(email));
        when(accountRepository.findById(any())).thenReturn(Optional.of(account));
        when(account.verifyValidSessionStatus(any()))
                .thenReturn(Result.success());
        AccessToken accessToken = new AccessToken(
                "newAccessTokenString",
                "Bearer",
                Instant.now()
        );
        when(tokenService.generateAccessToken(any()))
                .thenReturn(accessToken);
        AccessToken result = refreshSessionUseCase.execute("oldAccessTokenString");
        assertNotNull(result);
        assertEquals(accessToken, result);
        assertEquals("newAccessTokenString", result.value());

        verify(tokenService, times(1)).extractAccountInfo("oldAccessTokenString");
        verify(accountRepository, times(1)).findById(accountId);
        verify(account, times(1)).verifyValidSessionStatus(sessionId);
        verify(tokenService, times(1)).generateAccessToken(any());
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    public void testRefreshSessionAccountNotFound() {
        AccountInfo accountInfo = new AccountInfo(
                accountId,
                userId,
                sessionId,
                email
        );
        when(tokenService.extractAccountInfo(anyString()))
                .thenReturn(accountInfo);
        when(accountRepository.findById(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(InvalidClaimsException.class, () -> refreshSessionUseCase.execute("oldAccessTokenString"));
        assertTrue(exception.getMessage().contains("Account with id"));

        verify(tokenService, times(1)).extractAccountInfo("oldAccessTokenString");
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    @DisplayName("Should throw exception when session is invalid")
    public void testRefreshSessionInvalidSession() {
        AccountInfo accountInfo = new AccountInfo(
                accountId,
                userId,
                sessionId,
                email
        );
        when(tokenService.extractAccountInfo(anyString()))
                .thenReturn(accountInfo);
        Account account = mock(Account.class);
        when(accountRepository.findById(any())).thenReturn(Optional.of(account));
        when(account.verifyValidSessionStatus(any()))
                .thenReturn(Result.failure(DomainError.businessRule("Invalid session")));
        assertThrows(InvalidSessionException.class, () -> refreshSessionUseCase.execute("oldAccessTokenString"));

        verify(tokenService, times(1)).extractAccountInfo("oldAccessTokenString");
        verify(accountRepository, times(1)).findById(accountId);
        verify(account, times(1)).verifyValidSessionStatus(sessionId);
        verifyNoMoreInteractions(tokenService, accountRepository);
    }
}
