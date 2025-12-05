package toast.appback.src.users.application.communication.result;

import java.util.UUID;

/**
 * DTO de salida que representa la vista de un usuario para capas superiores (API, UI, etc.).
 *
 * @param userId    UUID público del usuario.
 * @param firstName Nombre de pila.
 * @param lastName  Apellido.
 * @param phone     Representación del teléfono (por ejemplo "+34-612345678").
 */
public record UserView(
        UUID userId,
        String firstName,
        String lastName,
        String phone
) {
}
