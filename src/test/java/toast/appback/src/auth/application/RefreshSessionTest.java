package toast.appback.src.auth.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.Jwt;
import toast.appback.src.auth.application.exceptions.InvalidClaimsException;
import toast.appback.src.auth.application.exceptions.domain.InvalidSessionException;
import toast.appback.src.auth.application.mother.AccountMother;
import toast.appback.src.auth.application.mother.TokenMother;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.application.usecase.implementation.RefreshSessionUseCase;
import toast.appback.src.auth.domain.*;
import toast.appback.src.auth.domain.repository.AccountRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Refresh session use case tests")
public class RefreshSessionTest {
    private RefreshSessionUseCase refreshSessionUseCase;
    private final TokenService tokenService = mock(TokenService.class);
    private final AccountRepository accountRepository = mock(AccountRepository.class);

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
        String refreshToken = "validRefreshToken";
        Account account = AccountMother.validAccount();
        Session session = account.startSession().get();
        TokenClaims claims = new TokenClaims(
                account.getAccountId(),
                account.getUserId(),
                session.getSessionId()
        );
        Jwt accessToken = TokenMother.createJwt("accessToken");

        when(tokenService.extractClaimsFromRefreshToken(refreshToken)).thenReturn(claims);

        when(accountRepository.findById(claims.accountId())).thenReturn(Optional.of(account));

        when(tokenService.generateAccessToken(claims)).thenReturn(accessToken);

        Jwt result = refreshSessionUseCase.execute(refreshToken);
        assertEquals(accessToken, result);

        verify(tokenService, times(1)).extractClaimsFromRefreshToken(refreshToken);
        verify(accountRepository, times(1)).findById(claims.accountId());
        verify(tokenService, times(1)).generateAccessToken(claims);

        verifyNoMoreInteractions(
                tokenService,
                accountRepository
        );
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    public void testRefreshSessionAccountNotFound() {
        String refreshToken = "validRefreshToken";
        TokenClaims claims = TokenMother.createClaims();

        when(tokenService.extractClaimsFromRefreshToken(refreshToken)).thenReturn(claims);

        when(accountRepository.findById(claims.accountId())).thenReturn(Optional.empty());

        assertThrows(InvalidClaimsException.class, () -> {
            refreshSessionUseCase.execute(refreshToken);
        });

        verify(tokenService, times(1)).extractClaimsFromRefreshToken(refreshToken);
        verify(accountRepository, times(1)).findById(claims.accountId());

        verifyNoMoreInteractions(
                tokenService,
                accountRepository
        );
    }

    @Test
    @DisplayName("Should throw exception when session is invalid")
    public void testRefreshSessionInvalidSession() {
        String refreshToken = "validRefreshToken";
        Account account = AccountMother.validAccount();
        Session session = account.startSession().get();
        session.revoke();
        TokenClaims claims = new TokenClaims(
                account.getAccountId(),
                account.getUserId(),
                session.getSessionId()
        );

        when(tokenService.extractClaimsFromRefreshToken(refreshToken)).thenReturn(claims);

        when(accountRepository.findById(claims.accountId())).thenReturn(Optional.of(account));

        assertThrows(InvalidSessionException.class, () -> {
            refreshSessionUseCase.execute(refreshToken);
        });

        verify(tokenService, times(1)).extractClaimsFromRefreshToken(refreshToken);
        verify(accountRepository, times(1)).findById(claims.accountId());

        verifyNoMoreInteractions(
                tokenService,
                accountRepository
        );
    }
}
