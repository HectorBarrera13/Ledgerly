package toast.appback.src.debts.infrastructure.service.transactional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.usecase.contract.EditDebtBetweenUsers;

/**
 * Envoltura transaccional para la edición de deudas entre usuarios.
 */
@Service
@RequiredArgsConstructor
public class EditDebtBetweenUsersService {
    private final EditDebtBetweenUsers editDebtBetweenUsers;

    /**
     * Ejecuta la edición dentro de una transacción.
     *
     * @param command Comando con la información de edición.
     * @return Vista pública de la deuda actualizada.
     */
    @Transactional
    public DebtBetweenUsersView execute(EditDebtCommand command) {
        return editDebtBetweenUsers.execute(command);
    }

}
