package toast.appback.src.auth.infrastructure.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.Jwt;
import toast.appback.src.auth.application.communication.result.Tokens;
import toast.appback.src.auth.application.port.TokenService;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.infrastructure.exceptions.TokenClaimsException;
import toast.appback.src.auth.infrastructure.exceptions.TokenExpiredException;
import toast.appback.src.users.domain.UserId;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JWTService implements TokenService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.access.seconds}")
    private long accessExpirationInSeconds;

    @Value("${jwt.expiration.refresh.seconds}")
    private long refreshExpirationInSeconds;

    private SecretKey secretKey;

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    @Override
    public Jwt generateAccessToken(TokenClaims tokenClaims) {
        return getJwt(tokenClaims, accessExpirationInSeconds, TOKEN_TYPE_ACCESS);
    }

    private Jwt generateRefreshToken(TokenClaims tokenClaims) {
        return getJwt(tokenClaims, refreshExpirationInSeconds, "refresh");
    }

    @Override
    public Tokens generateTokens(TokenClaims tokenClaims) {
        Jwt accessToken = generateAccessToken(tokenClaims);
        Jwt refreshToken = generateRefreshToken(tokenClaims);
        return new Tokens(accessToken, refreshToken);
    }

    private Jwt getJwt(TokenClaims tokenClaims, long sessionExpirationInSeconds, String type) {
        String accountId = tokenClaims.accountId().getValue().toString();
        String userId = tokenClaims.userId().getValue().toString();
        String sessionId = tokenClaims.sessionId().getValue().toString();
        String refreshToken = buildToken(accountId, userId, sessionId, sessionExpirationInSeconds, type);
        return new Jwt(
                refreshToken,
                Instant.now().plusSeconds(sessionExpirationInSeconds)
        );
    }

    @Override
    public TokenClaims extractClaimsFromRefreshToken(String refreshToken) {
        String type = extractClaim(refreshToken,
                claims -> claims.get(TOKEN_TYPE_CLAIM, String.class), true);
        if (!"refresh".equals(type)) {
            throw new TokenClaimsException("invalid token type");
        }
        return getTokenClaims(refreshToken, true);
    }

    @Override
    public TokenClaims extractClaimsFromAccessToken(String accessToken) {
        String type = extractClaim(accessToken,
                claims -> claims.get(TOKEN_TYPE_CLAIM, String.class), true);
        if (!TOKEN_TYPE_ACCESS.equals(type)) {
            throw new TokenClaimsException("invalid token type");
        }
        return getTokenClaims(accessToken, true);
    }

    @Override
    public TokenClaims extractClaimsFromAccessTokenUnsafe(String refreshToken) {
        String type = extractClaim(refreshToken,
                claims -> claims.get(TOKEN_TYPE_CLAIM, String.class), false);
        if (!TOKEN_TYPE_ACCESS.equals(type)) {
            throw new TokenClaimsException("invalid token type");
        }
        return getTokenClaims(refreshToken, false);
    }

    private TokenClaims getTokenClaims(String token, boolean safe) {
        String userId = extractClaim(token, Claims::getSubject, safe);

        String accountId = extractClaim(token, Claims::getId, safe);

        String sessionId = extractClaim(token,
                claims -> claims.get("session", String.class), safe);

        return new TokenClaims(
                AccountId.load(UUID.fromString(accountId)),
                UserId.load(UUID.fromString(userId)),
                SessionId.load(UUID.fromString(sessionId))
        );
    }

    private String buildToken(final String accountId, final String userId, final String sessionId, final long expirationTimeInSeconds, String type) {
        return Jwts.builder()
                .id(accountId)
                .subject(userId)
                .claim("session", sessionId)
                .claim(TOKEN_TYPE_CLAIM, type)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTimeInSeconds * 1000))
                .signWith(secretKey)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, boolean safe) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return claimsResolver.apply(claims);
        } catch (SignatureException e) {
            logger.warn("JWT signature inválida: {}", e.getMessage());
            throw new TokenClaimsException("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            logger.warn("JWT value malformado: {}", e.getMessage());
            throw new TokenClaimsException("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            if (safe) {
                logger.warn("JWT token expirado: {}", e.getMessage());
                throw new TokenExpiredException();
            } else {
                return claimsResolver.apply(e.getClaims());
            }
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT value no soportado: {}", e.getMessage());
            throw new TokenClaimsException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string vacío o nulo: {}", e.getMessage());
            throw new TokenClaimsException("JWT claims string is empty or null", e);
        } catch (Exception e) {
            logger.error("Error inesperado al extraer claims del JWT - Token: {}, Safe mode: {}, Error: {}",
                    token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "null",
                    safe,
                    e.getMessage(),
                    e);
            throw new TokenClaimsException("Error extracting JWT claims: " + e.getMessage(), e);
        }
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public void setAccessExpirationInSeconds(long accessExpirationInSeconds) {
        this.accessExpirationInSeconds = accessExpirationInSeconds;
    }
}
