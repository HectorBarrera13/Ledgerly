package toast.appback.src.auth.infrastructure.api.dto.response;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        String email
) {
}
