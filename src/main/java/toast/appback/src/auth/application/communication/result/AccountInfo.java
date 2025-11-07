package toast.appback.src.auth.application.communication.result;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;

public record AccountInfo(
        AccountId accountId,
        SessionId sessionId,
        String email
) {
}
