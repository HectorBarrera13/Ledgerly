package toast.appback.src.groups.application.communication.result;

import toast.appback.src.users.application.communication.result.UserView;
import toast.model.entities.CursorIdentifiable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GroupView (
        UUID groupId,
        String name,
        String description,
        Instant createdAt
) implements CursorIdentifiable<UUID> {
    @Override
    public UUID getCursorId() {
        return this.groupId;
    }
}
