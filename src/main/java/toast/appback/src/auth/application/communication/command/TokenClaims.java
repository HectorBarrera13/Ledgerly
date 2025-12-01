package toast.appback.src.auth.application.communication.command;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;
import toast.appback.src.users.domain.UserId;

public record TokenClaims(
        AccountId accountId,
        UserId userId,
        SessionId sessionId
) {
}
