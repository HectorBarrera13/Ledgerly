package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

import java.util.UUID;

public class FriendNotFound extends ApplicationException {
    private final UUID userId;

    public FriendNotFound(UUID userId, String message) {
        super(message);
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
