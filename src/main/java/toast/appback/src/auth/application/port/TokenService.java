package toast.appback.src.auth.application.port;
import toast.appback.src.auth.application.communication.command.TokenClaims;
import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.communication.result.AccessToken;

public interface TokenService {
    AccessToken generateAccessToken(TokenClaims tokenClaims);
    AccountInfo extractAccountInfo(String token);
}