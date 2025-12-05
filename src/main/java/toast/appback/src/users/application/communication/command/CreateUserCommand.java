package toast.appback.src.users.application.communication.command;

/**
 * Comando para la creación de un usuario.
 *
 * <p>Contiene los datos necesarios que el caso de uso `CreateUser` requiere para crear
 * un nuevo usuario en el sistema.
 * <p>
 * Campos:
 *
 * @param firstName        Nombre de pila del usuario.
 * @param lastName         Apellido del usuario.
 * @param phoneCountryCode Código de país del teléfono (ej. "+34").
 * @param phoneNumber      Número de teléfono (solo dígitos).
 */
public record CreateUserCommand(
        String firstName,
        String lastName,
        String phoneCountryCode,
        String phoneNumber
) {
}