package toast.appback.src.auth.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

public class AccountExistsException extends ApplicationException {
    private final String email;

    public AccountExistsException(String email) {
        super("Account with email " + email + " already exists.");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
