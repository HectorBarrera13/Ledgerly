package toast.appback.src.auth.application.communication.result;

import java.util.UUID;

/**
 * Vista pública de una cuenta para ser consumida por capas superiores (API, UI).
 *
 * @param accountId Identificador público de la cuenta.
 * @param email     Email asociado a la cuenta.
 */
public record AccountView(
        UUID accountId,
        String email
) {
}
