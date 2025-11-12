package toast.appback.src.users.application.exceptions;

import java.util.UUID;

public class RequesterNotFound extends RuntimeException {
    private final UUID userId;

    public RequesterNotFound(UUID userId, String message) {
        super(message);
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
