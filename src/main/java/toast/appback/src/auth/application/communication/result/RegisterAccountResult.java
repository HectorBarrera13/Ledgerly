package toast.appback.src.auth.application.communication.result;

import toast.appback.src.auth.domain.Account;
import toast.appback.src.users.domain.User;

public record RegisterAccountResult(
        User user,
        Account account,
        AccessToken accessToken
) {
}
