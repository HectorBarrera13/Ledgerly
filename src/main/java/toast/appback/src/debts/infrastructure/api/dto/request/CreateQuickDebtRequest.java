package toast.appback.src.debts.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.debts.application.communication.command.CreateQuickDebtCommand;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

public record CreateQuickDebtRequest (
        String purpose,
        String description,
        String currency,
        Long amount,
        @JsonProperty("my_role")
        String role,
        @JsonProperty("target_user_name")
        String targetUserName
){
    public CreateQuickDebtCommand toCreateQuickDebtCommand(UserId userId) {
        return new CreateQuickDebtCommand(
                purpose,
                description,
                currency,
                amount,
                userId,
                role,
                targetUserName
        );
    }

}
