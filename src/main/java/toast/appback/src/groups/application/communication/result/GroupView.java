package toast.appback.src.groups.application.communication.result;

import toast.model.entities.CursorIdentifiable;

import java.time.Instant;
import java.util.UUID;

/**
 * Vista pública resumida de un grupo para listados.
 *
 * @param groupId     Identificador del grupo.
 * @param creatorId   Identificador del usuario creador.
 * @param name        Nombre del grupo.
 * @param description Descripción del grupo.
 * @param createdAt   Fecha de creación.
 */
public record GroupView(
        UUID groupId,
        UUID creatorId,
        String name,
        String description,
        Instant createdAt
) implements CursorIdentifiable<UUID> {
    @Override
    public UUID getCursorId() {
        return this.groupId;
    }
}
