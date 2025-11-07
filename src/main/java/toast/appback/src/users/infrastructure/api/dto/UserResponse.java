package toast.appback.src.users.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UserResponse(
        UUID id,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        String email,
        String phone
) {
}
