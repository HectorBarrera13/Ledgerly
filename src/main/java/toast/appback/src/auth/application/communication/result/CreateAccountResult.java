package toast.appback.src.auth.application.communication.result;

import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;

/**
 * Resultado devuelto tras crear una cuenta: la entidad de cuenta y la sesión inicial creada.
 *
 * @param account Entidad de dominio `Account` creada.
 * @param session Sesión inicial generada para la cuenta.
 */
public record CreateAccountResult(
        Account account,
        Session session
) {
}
