package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

public class RemoveMySelfFromFriendsException extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "a user cannot remove himself from his friends list.";
    private static final String FRIENDLY_MESSAGE = "you cannot remove yourself from your friends list.";

    public RemoveMySelfFromFriendsException() {
        super(MESSAGE_TEMPLATE, FRIENDLY_MESSAGE);
    }
}
