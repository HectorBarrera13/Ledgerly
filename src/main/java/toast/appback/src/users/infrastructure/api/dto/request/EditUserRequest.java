package toast.appback.src.users.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import toast.appback.src.users.application.communication.command.EditUserCommand;
import toast.appback.src.users.domain.UserId;

public record EditUserRequest(
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName
) {
    public EditUserCommand toCommand(UserId userId) {
        return new EditUserCommand(
                userId,
                this.firstName,
                this.lastName
        );
    }
}
