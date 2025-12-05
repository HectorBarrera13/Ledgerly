package toast.appback.src.groups.application.communication.result;

import toast.model.entities.CursorIdentifiable;

import java.util.UUID;

/**
 * Vista pública resumida de un miembro del grupo.
 *
 * @param userId    Identificador del usuario.
 * @param firstName Nombre.
 * @param lastName  Apellidos.
 * @param phone     Teléfono de contacto.
 */
public record MemberView(
        UUID userId,
        String firstName,
        String lastName,
        String phone

) implements CursorIdentifiable<UUID> {
    @Override
    public UUID getCursorId() {
        return this.userId;
    }
}
