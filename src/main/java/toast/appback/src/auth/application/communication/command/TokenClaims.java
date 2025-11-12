package toast.appback.src.auth.application.communication.command;

import toast.appback.src.auth.domain.AccountId;
import toast.appback.src.auth.domain.SessionId;

public record TokenClaims(
        AccountId accountId,
        SessionId sessionId,
        String email
) {
}
