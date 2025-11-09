package toast.appback.src.users.application.communication.result;

import java.util.UUID;

public record UserQueryResult(
        UUID id,
        String firstName,
        String lastName,
        String phone
) {
}
