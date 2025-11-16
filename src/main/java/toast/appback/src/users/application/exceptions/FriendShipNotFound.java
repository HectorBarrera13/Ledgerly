package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;
import toast.appback.src.users.domain.UserId;

public class FriendShipNotFound extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "friendship between user %s and user %s not found.";
    private static final String FRIENDLY_MESSAGE = "friend not found.";
    private final UserId userAId;
    private final UserId userBId;

    public FriendShipNotFound(UserId userAId, UserId userBId) {
        super(
                String.format(MESSAGE_TEMPLATE, userAId.getValue(), userBId.getValue()),
                FRIENDLY_MESSAGE
        );
        this.userAId = userAId;
        this.userBId = userBId;

    }

    public UserId getUserAId() {
        return userAId;
    }

    public UserId getUserBId() {
        return userBId;
    }
}
