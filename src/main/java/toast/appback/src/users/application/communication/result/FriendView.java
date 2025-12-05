package toast.appback.src.users.application.communication.result;

import toast.model.entities.CursorIdentifiable;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de salida que representa la vista de un amigo en listados.
 *
 * @param userId    UUID público del amigo.
 * @param firstName Nombre de pila.
 * @param lastName  Apellido.
 * @param phone     Teléfono formateado.
 * @param addedAt   Fecha en la que se añadió la amistad.
 */
public record FriendView(
        UUID userId,
        String firstName,
        String lastName,
        String phone,
        Instant addedAt
) implements CursorIdentifiable<UUID> {
    /**
     * Cursor usado para paginación; se mapea al UUID del usuario.
     *
     * @return El UUID que actúa como cursor para este registro.
     */
    @Override
    public UUID getCursorId() {
        return this.userId;
    }
}
