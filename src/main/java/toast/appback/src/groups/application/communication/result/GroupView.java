package toast.appback.src.groups.application.communication.result;

import java.util.UUID;

public record GroupView(
        UUID groupId,
        String name,
        String description
) {
}
