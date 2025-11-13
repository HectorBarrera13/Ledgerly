package toast.appback.src.auth.infrastrcuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.infrastructure.exceptions.TokenClaimsException;
import toast.appback.src.auth.infrastructure.exceptions.TokenExpiredException;
import toast.appback.src.auth.infrastructure.service.JWTService;
import toast.appback.src.users.domain.UserId;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWTService partial integration tests")
class JWTServiceTest {
    private JWTService jwtService;

    private final AccountId accountId = AccountId.generate();
    private final UserId userId = UserId.generate();
    private final SessionId sessionId = SessionId.generate();

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
        jwtService.setJwtSecret("SuperSecretKeyForJWTsThatIsAtLeast32BytesLong!");
        jwtService.setAccessExpiration(1000L * 60L); // 1 min
        jwtService.init();
    }

    @Test
    @DisplayName("Should generate and extract account info successfully")
    void shouldGenerateAndExtractAccountInfo() {
        TokenClaims claims = new TokenClaims(accountId, userId, sessionId, "test@example.com");
        AccessToken token = jwtService.generateAccessToken(claims);

        assertNotNull(token);
        assertNotNull(token.value());
        assertEquals("Bearer", token.type());

        AccountInfo info = jwtService.extractAccountInfo(token.value());

        assertEquals(accountId, info.accountId());
        assertEquals(sessionId, info.sessionId());
        assertEquals("test@example.com", info.email());
    }

    @Test
    @DisplayName("Should throw TokenExpiredException when token is expired")
    void shouldThrowWhenExpired() throws InterruptedException {
        jwtService.setAccessExpiration(10); // 10 ms
        jwtService.init();

        TokenClaims claims = new TokenClaims(accountId, userId, sessionId, "expired@example.com");

        AccessToken token = jwtService.generateAccessToken(claims);

        Thread.sleep(15);

        assertThrows(TokenExpiredException.class, () -> jwtService.extractAccountInfo(token.value()));
    }

    @Test
    @DisplayName("Should throw TokenClaimsException for malformed token")
    void shouldThrowForMalformedToken() {
        String badToken = "this.is.not.jwt";
        assertThrows(TokenClaimsException.class, () -> jwtService.extractAccountInfo(badToken));
    }
}
