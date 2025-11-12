package toast.appback.src.users.application.exceptions;

import toast.appback.src.shared.application.ApplicationException;

import java.util.UUID;

public class ReceiverNotFound extends ApplicationException {
    private final UUID userId;

    public ReceiverNotFound(UUID userId, String message) {
        super(message);
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
