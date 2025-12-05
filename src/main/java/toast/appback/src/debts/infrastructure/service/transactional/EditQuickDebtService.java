package toast.appback.src.debts.infrastructure.service.transactional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.usecase.contract.EditQuickDebt;

/**
 * Envoltura transaccional para la edición de QuickDebt (deuda rápida).
 */
@Service
@RequiredArgsConstructor
public class EditQuickDebtService {
    private final EditQuickDebt editQuickDebt;

    /**
     * Ejecuta la edición dentro de una transacción.
     *
     * @param editDebtCommand Comando con los datos de edición.
     * @return Vista pública de la deuda actualizada.
     */
    @Transactional
    public QuickDebtView execute(EditDebtCommand editDebtCommand) {
        return editQuickDebt.execute(editDebtCommand);
    }
}
