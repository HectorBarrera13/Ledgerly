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
import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.auth.infrastructure.exceptions.TokenClaimsException;
import toast.appback.src.auth.infrastructure.exceptions.TokenExpiredException;
import toast.appback.src.auth.application.port.TokenService;
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

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.access}")
    private long accessExpiration;

    private SecretKey secretKey;

    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    @Override
    public AccessToken generateAccessToken(TokenClaims tokenClaims) {
        String accountId = tokenClaims.accountId().getValue().toString();
        String userId = tokenClaims.userId().getValue().toString();
        String sessionId = tokenClaims.sessionId().getValue().toString();
        String email = tokenClaims.email();
        String accessToken = buildToken(accountId, email, userId, sessionId, accessExpiration);
        return new AccessToken(
                accessToken,
                "Bearer",
                Instant.now().plusSeconds(accessExpiration)
        );
    }

    @Override
    public AccountInfo extractAccountInfo(String token) {
        if (isTokenExpired(token)) {
            throw new TokenExpiredException();
        }

        String subject = extractClaim(token, Claims::getSubject);

        String accountId = extractClaim(token, Claims::getId);

        String userId = extractClaim(token,
                claims -> claims.get("userId", String.class));

        String sessionId = extractClaim(token,
                claims -> claims.get("sessionId", String.class));

        return new AccountInfo(
                AccountId.load(UUID.fromString(accountId)),
                UserId.load(UUID.fromString(userId)),
                SessionId.load(UUID.fromString(sessionId)),
                subject
        );
    }

    private String buildToken(final String id, final String subject, final String userId, final String sessionId, final long expirationTime) {
        return Jwts.builder()
                .id(id)
                .subject(subject)
                .claim("sessionId", sessionId)
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
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
            logger.warn("JWT value expirado: {}", e.getMessage());
            throw new TokenExpiredException();
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT value no soportado: {}", e.getMessage());
            throw new TokenClaimsException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string vacío o nulo: {}", e.getMessage());
            throw new TokenClaimsException("JWT claims string is empty or null", e);
        } catch (Exception e) {
            logger.error("Error al extraer claims del JWT: {}", e.getMessage());
            throw new TokenClaimsException("Error extracting JWT claims", e);
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration)
                .before(new Date());
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public void setAccessExpiration(long accessExpiration) {
        this.accessExpiration = accessExpiration;
    }
}
