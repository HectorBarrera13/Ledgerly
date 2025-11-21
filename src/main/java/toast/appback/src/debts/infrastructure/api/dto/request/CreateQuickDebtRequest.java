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
        @JsonProperty("user_id")
        UserId userId,
        String role,
        @JsonProperty("target_user")
        String targetUser
){
    public CreateQuickDebtCommand toCreateQuickDebtCommand() {
        return new CreateQuickDebtCommand(
                purpose,
                description,
                currency,
                amount,
                userId,
                role,
                targetUser
        );
    }

}
