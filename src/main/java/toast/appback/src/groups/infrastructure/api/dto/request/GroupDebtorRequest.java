package toast.appback.src.groups.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.groups.application.communication.command.GroupDebtorCommand;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

public record GroupDebtorRequest(
        @JsonProperty("debtor_id")
        UUID debtorId,
        Long amount
) {
    public GroupDebtorCommand toDebtorCommand() {
        return new GroupDebtorCommand(
                UserId.load(debtorId),
                amount
        );
    }
}
