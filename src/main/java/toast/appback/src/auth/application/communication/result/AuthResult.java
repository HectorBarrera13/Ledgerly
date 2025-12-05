package toast.appback.src.auth.application.communication.result;

import toast.appback.src.users.application.communication.result.UserView;

/**
 * Resultado de una operación de autenticación que incluye información de cuenta, usuario y tokens.
 *
 * @param account Vista pública de la cuenta autenticada.
 * @param user    Vista pública del usuario asociado.
 * @param tokens  Tokens generados (access + refresh).
 */
public record AuthResult(
        AccountView account,
        UserView user,
        Tokens tokens
) {
}
