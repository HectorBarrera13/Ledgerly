package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

public class FriendToMySelfException extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "cannot add yourself as a friend.";
    private static final String FRIENDLY_MESSAGE = "you cannot add yourself as a friend.";
    public FriendToMySelfException() {
        super(MESSAGE_TEMPLATE, FRIENDLY_MESSAGE);
    }
}
