package toast.appback.src.debts.application.usecase.contract;

import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;

public interface EditQuickDebtStatus {
    QuickDebtView execute(EditDebtStatusCommand command);
}
