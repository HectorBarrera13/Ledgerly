package toast.appback.src.auth.infrastructure.exceptions;

public class AuthenticationServiceException extends RuntimeException {

    public AuthenticationServiceException(Throwable cause) {
        super("Authentication service encountered an unexpected error.", cause);
    }
}
