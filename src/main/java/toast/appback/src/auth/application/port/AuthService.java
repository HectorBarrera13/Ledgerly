package toast.appback.src.auth.application.port;

import toast.appback.src.auth.application.communication.command.AuthenticateAccountCommand;

/**
 * Puerto de aplicación que abstrae el servicio de autenticación.
 * <p>
 * Implementaciones deben encargarse de la orquestación de la autenticación (validación de credenciales,
 * emisión de tokens, gestión de sesiones, etc.).
 */
public interface AuthService {
    /**
     * Autentifica un usuario con las credenciales proporcionadas en el comando.
     *
     * @param command Comando que contiene las credenciales de acceso.
     */
    void authenticate(AuthenticateAccountCommand command);
}