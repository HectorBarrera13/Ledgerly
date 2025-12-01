package toast.appback.src.users.application.communication.result;

import java.util.UUID;

public record UserView(
        UUID userId,
        String firstName,
        String lastName,
        String phone
) {
}
