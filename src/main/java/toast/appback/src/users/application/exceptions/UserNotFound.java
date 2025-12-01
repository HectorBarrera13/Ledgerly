package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;
import toast.appback.src.users.domain.UserId;

public class UserNotFound extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "user with id %s not found.";
    private static final String FRIENDLY_MESSAGE = "user not found.";
    private final UserId userId;

    public UserNotFound(UserId userId) {
        super(String.format(MESSAGE_TEMPLATE, userId), FRIENDLY_MESSAGE);
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }
}
