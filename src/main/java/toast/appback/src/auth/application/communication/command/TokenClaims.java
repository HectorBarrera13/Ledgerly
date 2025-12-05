package toast.appback.src.auth.application.communication.command;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.users.domain.UserId;

/**
 * Claims que se incluyen en los tokens JWT internos del sistema.
 *
 * @param accountId Identificador de la cuenta.
 * @param userId    Identificador del usuario asociado.
 * @param sessionId Identificador de la sesión.
 */
public record TokenClaims(
        AccountId accountId,
        UserId userId,
        SessionId sessionId
) {
}
