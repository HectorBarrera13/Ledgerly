package toast.appback.src.groups.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.groups.application.communication.command.AddGroupDebtCommand;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

public record AddGroupDebtRequest(
        @JsonProperty("group_id")
        UUID groupId,
        String purpose,
        String description,
        String currency,
        List<GroupDebtorRequest> debtors
) {
    public AddGroupDebtCommand toAddGroupDebtCommand(GroupId groupId, UserId creditorId) {
        return new AddGroupDebtCommand(
                groupId,
                creditorId,
                purpose,
                description,
                currency,
                debtors.stream()
                        .map(GroupDebtorRequest::toDebtorCommand)
                        .toList()
        );
    }
}
