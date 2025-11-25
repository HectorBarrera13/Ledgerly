package toast.appback.src.debts.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

public record CreateDebtBetweenUsersRequest(
    String purpose,
    String description,
    String currency,
    Long amount,
    @JsonProperty("my_role")
    String role,
    @JsonProperty("target_user_id")
    UUID targetId
) {
    public CreateDebtBetweenUsersCommand toCreateDebtBetweenUsersCommand(UserId actorId) {
        UserId targetId = UserId.load(this.targetId);
        if(role == null){
            throw new IllegalArgumentException("Role must be provided");
        }
        if(role.equalsIgnoreCase("debtor")){
            return new CreateDebtBetweenUsersCommand(
                    purpose,
                    description,
                    currency,
                    amount,
                    actorId,
                    targetId
            );
        } else if (role.equalsIgnoreCase("creditor")) {
            return new CreateDebtBetweenUsersCommand(
                    purpose,
                    description,
                    currency,
                    amount,
                    targetId,
                    actorId
            );
        }
        throw new IllegalArgumentException("Role must be either 'creditor' or 'debtor'");
    }
}
