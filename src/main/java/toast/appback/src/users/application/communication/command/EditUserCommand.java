package toast.appback.src.users.application.communication.command;

import toast.appback.src.users.domain.UserId;

/**
 * Comando para editar los datos de un usuario existente.
 *
 * @param userId    Identificador del usuario a editar.
 * @param firstName Nuevo nombre de pila.
 * @param lastName  Nuevo apellido.
 */
public record EditUserCommand(
        UserId userId,
        String firstName,
        String lastName
) {
}
