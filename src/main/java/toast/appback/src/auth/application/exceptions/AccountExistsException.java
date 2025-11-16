package toast.appback.src.auth.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

public class AccountExistsException extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "account with email %s already exists.";
    private static final String FRIENDLY_MESSAGE = "an account with the provided email already exists.";
    private final String email;

    public AccountExistsException(String email) {
        super(String.format(MESSAGE_TEMPLATE, email), FRIENDLY_MESSAGE);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
