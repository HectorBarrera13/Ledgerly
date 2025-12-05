package toast.appback.src.debts.application.communication.result;

import toast.model.entities.CursorIdentifiable;

import java.util.UUID;

public record UserSummaryView(
        UUID userId,
        String userFirstName,
        String userLastName
) implements CursorIdentifiable<UUID> {
    @Override
    public UUID getCursorId() {
        return this.userId;
    }
}
