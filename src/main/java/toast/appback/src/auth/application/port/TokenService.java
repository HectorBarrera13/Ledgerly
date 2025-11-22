package toast.appback.src.auth.application.port;
import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.Jwt;
import toast.appback.src.auth.application.communication.result.Tokens;

public interface TokenService {
    Tokens generateTokens(TokenClaims tokenClaims);
    Jwt generateAccessToken(TokenClaims tokenClaims);
    TokenClaims extractClaimsFromRefreshToken(String refreshToken);
    TokenClaims extractClaimsFromAccessTokenUnsafe(String refreshToken);
    TokenClaims extractClaimsFromAccessToken(String accessToken);
    boolean isTokenExpired(String token);
}