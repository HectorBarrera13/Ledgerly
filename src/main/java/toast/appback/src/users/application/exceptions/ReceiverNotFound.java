package toast.appback.src.users.application.exceptions;

import java.util.UUID;

public class ReceiverNotFound extends RuntimeException {
    private final UUID userId;

    public ReceiverNotFound(UUID userId, String message) {
        super(message);
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
