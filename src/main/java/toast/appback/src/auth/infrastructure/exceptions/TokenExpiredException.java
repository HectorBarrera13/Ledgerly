package toast.appback.src.auth.infrastructure.exceptions;

public class TokenExpiredException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Token has expired";
    public TokenExpiredException() {
        super(DEFAULT_MESSAGE);
    }
}
