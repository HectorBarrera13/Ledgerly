package toast.appback.src.auth.application.exceptions;

public class AccountExistsException extends RuntimeException {
    private final String email;

    public AccountExistsException(String email) {
        super("Account with email " + email + " already exists.");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
