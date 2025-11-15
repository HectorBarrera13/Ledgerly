package toast.appback.src.auth.application.communication.result;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.users.domain.UserId;

public record AccountInfo(
        AccountId accountId,
        UserId userId,
        SessionId sessionId
) {
}
