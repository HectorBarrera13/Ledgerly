package toast.appback.src.groups.infrastructure.api.dto.response;

import java.time.Instant;
import java.util.UUID;

public record GroupResponse(
        UUID groupId,
        String name,
        String description,
        Instant createdAt
) {
}
