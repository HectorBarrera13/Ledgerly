package toast.appback.src.groups.application.communication.command;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

import java.util.List;

public record AddGroupDebtCommand(
        GroupId groupId,
        UserId creditorId,
        String purpose,
        String description,
        String currency,
        List<GroupDebtorCommand> debtors
) {
}

