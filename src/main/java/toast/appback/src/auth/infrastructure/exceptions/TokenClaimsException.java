package toast.appback.src.auth.infrastructure.exceptions;

public class TokenClaimsException extends RuntimeException {
    public TokenClaimsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenClaimsException(String message) {
        super(message);
    }
}
