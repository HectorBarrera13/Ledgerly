package toast.appback.src.auth.infrastructure.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.shared.types.Result;
import toast.appback.src.shared.errors.AppError;
import toast.appback.src.auth.application.port.TokenService;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JWTService implements TokenService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.access}")
    private long accessExpiration;

    @Value("${jwt.expiration.refresh}")
    private long refreshExpiration;

    private SecretKey secretKey;

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    @Override
    public Result<TokenInfo, AppError> generateAccessToken(String uuid, String sessionId, String email) {
        String accessToken = buildToken(uuid, email, sessionId, accessExpiration);
        TokenInfo tokenResult = new TokenInfo(
                accessToken,
                "Bearer",
                Instant.now().plusSeconds(accessExpiration)
        );
        return Result.success(tokenResult);
    }

    @Override
    public Result<TokenInfo, AppError> generateRefreshToken(String uuid, String sessionId, String email) {
        String refreshToken = buildToken(uuid, email, sessionId, refreshExpiration);
        TokenInfo tokenResult = new TokenInfo(
                refreshToken,
                "Bearer",
                Instant.now().plusSeconds(refreshExpiration)
        );
        return Result.success(tokenResult);
    }

    @Override
    public Result<AccountInfo, AppError> extractClaims(String token) {
        Result<String, AppError> subjectResult = extractClaim(token, Claims::getSubject);
        if (subjectResult.isFailure()) {
            return Result.failure(subjectResult.getErrors());
        }

        Result<String, AppError> idResult = extractClaim(token, Claims::getId);
        if (idResult.isFailure()) {
            return Result.failure(idResult.getErrors());
        }
        String id = idResult.getValue();

        Result<String, AppError> sessionIdResult = extractClaim(token, claims -> claims.get("sessionId", String.class));
        if (sessionIdResult.isFailure()) {
            return Result.failure(sessionIdResult.getErrors());
        }
        String sessionId = sessionIdResult.getValue();

        AccountInfo accountInfo = new AccountInfo(
                AccountId.create(UUID.fromString(id)),
                SessionId.create(UUID.fromString(sessionId)),
                subjectResult.getValue()
        );
        return Result.success(accountInfo);
    }

    @Override
    public Result<Void, AppError> verifyToken(String token) {
        if (isTokenExpired(token)) {
            return Result.failure(AppError.authorizationFailed("Token has expired"));
        }
        return Result.success();
    }

    private String buildToken(final String id, final String subject, final String sessionId, final long expirationTime) {
        return Jwts.builder()
                .id(id)
                .subject(subject)
                .claim("sessionId", sessionId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    private <T> Result<T, AppError> extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return Result.success(claimsResolver.apply(claims));
        } catch (SignatureException e) {
            logger.warn("JWT signature inválida: {}", e.getMessage());
            return Result.failure(AppError.authorizationFailed("Invalid JWT signature").withDetails(e.getMessage()));
        } catch (MalformedJwtException e) {
            logger.warn("JWT token malformado: {}", e.getMessage());
            return Result.failure(AppError.authorizationFailed("Malformed JWT token").withDetails(e.getMessage()));
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expirado: {}", e.getMessage());
            return Result.failure(AppError.authorizationFailed("Expired JWT token").withDetails(e.getMessage()));
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT token no soportado: {}", e.getMessage());
            return Result.failure(AppError.authorizationFailed("Unsupported JWT token").withDetails(e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string vacío o nulo: {}", e.getMessage());
            return Result.failure(AppError.authorizationFailed("JWT claims string is empty or null").withDetails(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al extraer claim del JWT: {}", e.getMessage(), e);
            return Result.failure(AppError.authorizationFailed("Unexpected error while extracting JWT claim").withDetails(e.getMessage()));
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration)
                .map(d -> d.before(new Date()))
                .isSuccess();
    }
}
