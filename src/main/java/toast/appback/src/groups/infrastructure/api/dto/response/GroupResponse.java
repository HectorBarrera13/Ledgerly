package toast.appback.src.groups.infrastructure.api.dto.response;

import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GroupResponse(
        UUID groupId,
        String name,
        String description,
        Instant createdAt
) {
}
