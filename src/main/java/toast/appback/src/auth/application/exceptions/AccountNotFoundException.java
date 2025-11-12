package toast.appback.src.auth.application.exceptions;

public class AccountNotFoundException extends RuntimeException {
    private final String email;
    public AccountNotFoundException(String email) {
        super("Account with email " + email + " not found.");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
