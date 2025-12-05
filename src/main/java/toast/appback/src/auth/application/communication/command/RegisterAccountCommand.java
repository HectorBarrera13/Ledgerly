package toast.appback.src.auth.application.communication.command;

/**
 * Comando para registrar una nueva cuenta y usuario.
 * <p>
 * Contiene los datos necesarios para crear la entidad de usuario, su cuenta y el teléfono.
 *
 * @param firstName        Nombre de pila del usuario.
 * @param lastName         Apellido del usuario.
 * @param email            Email de la nueva cuenta.
 * @param password         Contraseña en texto plano.
 * @param phoneCountryCode Código de país del teléfono.
 * @param phoneNumber      Número de teléfono.
 */
public record RegisterAccountCommand(
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneCountryCode,
        String phoneNumber
) {
}
