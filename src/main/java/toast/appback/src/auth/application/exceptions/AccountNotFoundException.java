package toast.appback.src.auth.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

public class AccountNotFoundException extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "account with email %s not found.";
    private static final String FRIENDLY_MESSAGE = "the account with the provided email does not exist.";
    private final String email;

    public AccountNotFoundException(String email) {
        super(String.format(MESSAGE_TEMPLATE, email), FRIENDLY_MESSAGE);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
