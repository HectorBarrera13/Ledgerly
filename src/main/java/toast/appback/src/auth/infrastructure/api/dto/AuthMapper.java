package toast.appback.src.auth.infrastructure.api.dto;

import toast.appback.src.auth.application.communication.result.RegisterAccountResult;
import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.auth.infrastructure.api.dto.response.AccountLoginResponse;
import toast.appback.src.auth.infrastructure.api.dto.response.RefreshTokenResponse;
import toast.appback.src.auth.infrastructure.api.dto.response.RegisterAccountResponse;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;

public class AuthMapper {
    public static RegisterAccountResponse registerToResponse(RegisterAccountResult result) {
        UserView user = result.user();
        return new RegisterAccountResponse(
                new UserResponse(
                        user.userId(),
                        user.firstName(),
                        user.lastName(),
                        user.phone()
                ),
                result.email(),
                result.accessToken().value(),
                result.accessToken().expiresAt()
        );
    }

    public static AccountLoginResponse loginToResponse(AccessToken result) {
        return new AccountLoginResponse(
                result.value(),
                result.expiresAt()
        );
    }

    public static RefreshTokenResponse refreshToResponse(AccessToken result) {
        return new RefreshTokenResponse(
                result.value(),
                result.expiresAt()
        );
    }
}
