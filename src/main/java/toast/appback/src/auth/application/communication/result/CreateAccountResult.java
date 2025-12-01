package toast.appback.src.auth.application.communication.result;

import toast.appback.src.auth.domain.Account;
import toast.appback.src.auth.domain.Session;

public record CreateAccountResult(
        Account account,
        Session session
) {
}
