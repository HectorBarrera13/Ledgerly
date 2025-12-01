package toast.appback.src.auth.infrastructure.api.dto;

import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.application.communication.result.AuthResult;
import toast.appback.src.auth.infrastructure.api.dto.response.*;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;

public class AuthMapper {
    public static AuthResponse authToResponse(AuthResult result, boolean excludeRefreshToken) {
        AccountResponse accountResponse = new AccountResponse(
                result.account().accountId(),
                result.account().email()
        );
        UserResponse userResponse = new UserResponse(
                result.user().userId(),
                result.user().firstName(),
                result.user().lastName(),
                result.user().phone()
        );
        TokenResponse tokenResponse = new TokenResponse(
                result.tokens().accessToken().value(),
                result.tokens().accessToken().expiresAt(),
                excludeRefreshToken ? null : result.tokens().refreshToken().value()
        );
        return new AuthResponse(
                accountResponse,
                userResponse,
                tokenResponse
        );
    }

    public static AccountResponse toAccountResponse(AccountView accountView) {
        return new AccountResponse(
                accountView.accountId(),
                accountView.email()
        );
    }
}
