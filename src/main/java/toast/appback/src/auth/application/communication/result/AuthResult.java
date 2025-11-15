package toast.appback.src.auth.application.communication.result;

import toast.appback.src.users.application.communication.result.UserView;

public record AuthResult(
        AccountView account,
        UserView user,
        Tokens tokens
) {
}
