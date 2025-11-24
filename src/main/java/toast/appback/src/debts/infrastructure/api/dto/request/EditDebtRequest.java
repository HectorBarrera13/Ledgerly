package toast.appback.src.debts.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.users.domain.UserId;

public record EditDebtRequest (
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
    public EditDebtCommand toEditDebtCommand(UserId userId) {
        return new EditDebtCommand(
                userId,
                debtId,
                newPurpose,
                newDescription,
                newCurrency,
                newAmount
        );
    }
}
