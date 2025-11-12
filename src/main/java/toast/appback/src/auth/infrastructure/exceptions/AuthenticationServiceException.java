package toast.appback.src.auth.infrastructure.exceptions;

public class AuthenticationServiceException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Authentication service encountered an unexpected error.";
    public AuthenticationServiceException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
