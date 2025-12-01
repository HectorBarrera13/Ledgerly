package toast.appback.src.debts.infrastructure.api.dto.request;

import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

public record EditDebtStatusRequest(
        DebtId debtId,
        UserId actorId
) {
    public EditDebtStatusCommand EditDebtStatusRequest() {
        return new EditDebtStatusCommand(
                debtId,
                actorId
        );
    }
}
