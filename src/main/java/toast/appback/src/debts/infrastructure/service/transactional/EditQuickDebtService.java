package toast.appback.src.debts.infrastructure.service.transactional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.usecase.contract.EditQuickDebt;

@Service
@RequiredArgsConstructor
public class EditQuickDebtService {
    private final EditQuickDebt editQuickDebt;

    @Transactional
    public QuickDebtView execute(EditDebtCommand editDebtCommand) {
        return editQuickDebt.execute(editDebtCommand);
    }
}
