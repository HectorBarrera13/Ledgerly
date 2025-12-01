package toast.appback.src.auth.infrastructure.exceptions;

public class AuthenticationServiceException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "bad credentials";
    public AuthenticationServiceException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
