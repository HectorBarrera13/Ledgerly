package toast.appback.src.groups.application.communication.result;

import java.time.Instant;
import java.util.UUID;

public record MemberView(
        UUID userId,
        String firstName,
        String lastName,
        String phone,
        Instant addedAt
) {
}
