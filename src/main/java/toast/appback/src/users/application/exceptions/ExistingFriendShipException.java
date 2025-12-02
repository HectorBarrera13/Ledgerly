package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

public class ExistingFriendShipException extends ApplicationException {
    private static final String TEMPLATE_MESSAGE = "friendship already exists between users with ids %s and %s.";
    private static final String FRIENDLY_MESSAGE = "friendship already exists between the users.";
    private final UUID requestUser;
    private final UUID receiverUser;

    public ExistingFriendShipException(UserId requestUser, UserId receiverUser) {
        super(String.format(TEMPLATE_MESSAGE, requestUser.getValue(), receiverUser.getValue()), FRIENDLY_MESSAGE);
        this.requestUser = requestUser.getValue();
        this.receiverUser = receiverUser.getValue();
    }

    public UserId getRequestUser() {
        return UserId.load(requestUser);
    }

    public UserId getReceiverUser() {
        return UserId.load(receiverUser);
    }
}
