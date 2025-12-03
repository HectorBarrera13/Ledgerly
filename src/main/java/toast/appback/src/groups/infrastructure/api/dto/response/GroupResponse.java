package toast.appback.src.groups.infrastructure.api.dto.response;

import java.time.Instant;
import java.util.UUID;

public record GroupResponse(
        UUID groupId,
        UUID creatorId,
        String name,
        String description,
        Instant createdAt
) {
}
