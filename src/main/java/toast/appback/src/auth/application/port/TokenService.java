package toast.appback.src.auth.application.port;
import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.communication.result.AccessToken;

public interface TokenService {
    AccessToken generateAccessToken(String uuid, String sessionId, String email);
    AccountInfo extractAccountInfo(String token);
}