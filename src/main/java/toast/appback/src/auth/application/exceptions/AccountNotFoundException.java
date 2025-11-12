package toast.appback.src.auth.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

public class AccountNotFoundException extends ApplicationException {
    private final String email;
    public AccountNotFoundException(String email) {
        super("Account with email " + email + " not found.");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
