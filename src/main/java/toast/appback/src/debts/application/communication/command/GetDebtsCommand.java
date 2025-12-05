package toast.appback.src.debts.application.communication.command;

import toast.appback.src.users.domain.UserId;

/**
 * Comando para solicitar el listado de deudas relacionado con un usuario.
 *
 * @param actorId Usuario que solicita la lista (se filtra por su rol/deudas relacionadas).
 */
public record GetDebtsCommand(
        UserId actorId
) {
}
