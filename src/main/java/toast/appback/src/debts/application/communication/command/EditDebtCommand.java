package toast.appback.src.debts.application.communication.command;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

/**
 * Comando para editar campos básicos de una deuda (propósito, descripción y monto).
 *
 * @param userId         Usuario que solicita la edición (debe tener permisos).
 * @param debtId         Identificador de la deuda a editar.
 * @param newPurpose     Nuevo propósito.
 * @param newDescription Nueva descripción.
 * @param newCurrency    Nuevo código de moneda.
 * @param newAmount      Nuevo monto en centavos.
 */
public record EditDebtCommand(
        UserId userId,
        DebtId debtId,
        String newPurpose,
        String newDescription,
        String newCurrency,
        Long newAmount
) {
}
