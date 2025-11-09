package toast.appback.src.auth.application.port;
import toast.appback.src.auth.application.communication.result.AccountInfo;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.shared.utils.Result;
import toast.appback.src.shared.application.AppError;

public interface TokenService {
    Result<TokenInfo, AppError> generateAccessToken(String uuid, String sessionId, String email);
    Result<AccountInfo, AppError> extractClaims(String token);
    Result<Void, AppError> verifyToken(String token);
}