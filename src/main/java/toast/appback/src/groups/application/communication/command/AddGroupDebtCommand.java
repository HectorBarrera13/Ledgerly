package toast.appback.src.groups.application.communication.command;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

import java.util.List;

/**
 * Comando para crear una deuda asociada a un grupo.
 *
 * @param groupId     Identificador del grupo al que se añade la deuda.
 * @param creditorId  Identificador del usuario acreedor dentro del grupo.
 * @param purpose     Propósito de la deuda.
 * @param description Descripción adicional.
 * @param currency    Código ISO de la moneda.
 * @param debtors     Lista de deudores (ver {@link GroupDebtorCommand}).
 */
public record AddGroupDebtCommand(
        GroupId groupId,
        UserId creditorId,
        String purpose,
        String description,
        String currency,
        List<GroupDebtorCommand> debtors
) {
}
