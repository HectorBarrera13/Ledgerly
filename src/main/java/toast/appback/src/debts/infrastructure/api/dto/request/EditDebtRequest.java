package toast.appback.src.debts.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

public record EditDebtRequest (
        @JsonProperty("actor_id")
        UserId actorId,
        @JsonProperty("debt_id")
        DebtId debtId,
        @JsonProperty("new_purpose")
        String newPurpose,
        @JsonProperty("new_description")
        String newDescription,
        @JsonProperty("new_currency")
        String newCurrency,
        @JsonProperty("new_amount")
        Long newAmount
){
    public EditDebtCommand toEditDebtCommand() {
        return new EditDebtCommand(
                actorId,
                debtId,
                newPurpose,
                newDescription,
                newCurrency,
                newAmount
        );
    }
}
