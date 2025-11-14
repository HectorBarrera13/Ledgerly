package toast.appback.src.users.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EditUserRequest(
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName
) {
}
