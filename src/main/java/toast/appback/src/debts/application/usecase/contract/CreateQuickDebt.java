package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.CreateQuickDebtCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.shared.application.UseCaseFunction;

/**
 * Contrato del caso de uso para crear una deuda rápida (QuickDebt).
 * <p>
 * Implementaciones deben validar los datos, crear la entidad y devolver la vista pública creada.
 */
public interface CreateQuickDebt extends UseCaseFunction<QuickDebtView, CreateQuickDebtCommand> {
}
