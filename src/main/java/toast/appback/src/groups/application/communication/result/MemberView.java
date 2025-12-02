package toast.appback.src.groups.application.communication.result;

import toast.model.entities.CursorIdentifiable;

import java.util.UUID;

public record MemberView(
        UUID userId,
        String firstName,
        String lastName,
        String phone
) implements CursorIdentifiable<UUID>{
    @Override
    public UUID getCursorId() {
        return this.userId;
    }
}
