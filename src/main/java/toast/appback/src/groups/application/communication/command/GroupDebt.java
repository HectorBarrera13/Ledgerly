package toast.appback.src.groups.application.communication.command;

import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.groups.domain.vo.GroupId;

public record GroupDebt(
        GroupId groupId,
        DebtId debtId
) {
}
