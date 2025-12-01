package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;
import toast.appback.src.users.domain.UserId;

public class RequesterNotFound extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "requester with id %s not found.";
    private static final String FRIENDLY_MESSAGE = "the user who sent the friend request was not found.";
    private final UserId userId;

    public RequesterNotFound(UserId userId) {
        super(String.format(MESSAGE_TEMPLATE, userId.getValue()), FRIENDLY_MESSAGE);
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }
}
