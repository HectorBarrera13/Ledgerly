package toast.appback.src.users.application.communication.result;

import toast.model.entities.CursorIdentifiable;

import java.time.Instant;
import java.util.UUID;

public record FriendView(
        UUID userId,
        String firstName,
        String lastName,
        String phone,
        Instant addedAt
) implements CursorIdentifiable<UUID> {
    @Override
    public UUID getCursorId() {
        return this.userId;
    }
}
