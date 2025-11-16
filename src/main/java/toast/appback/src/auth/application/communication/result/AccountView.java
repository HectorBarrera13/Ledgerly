package toast.appback.src.auth.application.communication.result;

import java.util.UUID;

public record AccountView(
        UUID accountId,
        String email
) {
}
