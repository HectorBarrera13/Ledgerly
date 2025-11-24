package toast.appback.src.debts.application.communication.result;

import java.util.UUID;

public record UserSummaryView(
        UUID userId,
        String userFirstName,
        String userLastName
) {
}
