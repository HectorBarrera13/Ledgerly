package toast.appback.src.auth.application.communication.result;
import toast.appback.src.users.application.communication.result.UserView;

public record RegisterAccountResult(
        UserView user,
        String email,
        AccessToken accessToken
) {
}
