package toast.appback.src.auth.infrastructure.api.dto.response;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;

public record AuthResponse(
        AccountResponse account,
        UserResponse user,
        TokenResponse token
) {
}
