package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

public class FriendShipNotFound extends ApplicationException {
    private static final String MESSAGE_TEMPLATE = "friendship between user %s and user %s not found.";
    private static final String FRIENDLY_MESSAGE = "friend not found.";
    private final UUID userAId;
    private final UUID userBId;

    public FriendShipNotFound(UserId userAId, UserId userBId) {
        super(
                String.format(MESSAGE_TEMPLATE, userAId.getValue(), userBId.getValue()),
                FRIENDLY_MESSAGE
        );
        this.userAId = userAId.getValue();
        this.userBId = userBId.getValue();

    }

    public UserId getUserAId() {
        return UserId.load(userAId);
    }

    public UserId getUserBId() {
        return UserId.load(userBId);
    }
}
