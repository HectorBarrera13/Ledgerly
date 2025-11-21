package toast.appback.src.debts.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;
import toast.appback.src.users.domain.UserId;

public record CreateDebtBetweenUsersRequest(
    String purpose,
    String description,
    String currency,
    Long amount,
    @JsonProperty("debtor_id")
    UserId debtorId,
    @JsonProperty("debtor_id")
    UserId creditorId
) {
    public CreateDebtBetweenUsersCommand toCreateDebtBetweenUsersCommand() {
        return new CreateDebtBetweenUsersCommand(
                purpose,
                description,
                currency,
                amount,
                debtorId,
                creditorId
        );
    }
}
