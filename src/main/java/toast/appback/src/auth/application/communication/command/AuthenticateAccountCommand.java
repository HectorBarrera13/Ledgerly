package toast.appback.src.auth.application.communication.command;

/**
 * Comando con credenciales para autenticar una cuenta.
 *
 * @param email    Email de la cuenta.
 * @param password Contraseña en texto plano.
 */
public record AuthenticateAccountCommand(
        String email,
        String password
) {
}
