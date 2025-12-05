package toast.appback.src.auth.application.communication.command;

import toast.appback.src.users.domain.UserId;

/**
 * Comando para crear una cuenta asociada a un `User` existente.
 *
 * @param userId   Identificador del usuario asociado.
 * @param email    Email para la cuenta.
 * @param password Contraseña en texto plano (será hasheada por el dominio o infraestructura).
 */
public record CreateAccountCommand(
        UserId userId,
        String email,
        String password
) {
}
