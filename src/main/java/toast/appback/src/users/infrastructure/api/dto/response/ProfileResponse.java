package toast.appback.src.users.infrastructure.api.dto.response;

import toast.appback.src.auth.infrastructure.api.dto.response.AccountResponse;

public record ProfileResponse(
        AccountResponse account,
        UserResponse user
) {
}
