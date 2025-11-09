package toast.appback.src.auth.infrastructure.api.dto;

import toast.appback.src.auth.application.communication.result.RegisterAccountResult;
import toast.appback.src.auth.application.communication.result.TokenInfo;
import toast.appback.src.auth.infrastructure.api.dto.response.AccountLoginResponse;
import toast.appback.src.auth.infrastructure.api.dto.response.RefreshTokenResponse;
import toast.appback.src.auth.infrastructure.api.dto.response.RegisterAccountResponse;
import toast.appback.src.users.domain.User;
import toast.appback.src.users.infrastructure.api.dto.UserResponse;

public class AuthMapper {
    public static RegisterAccountResponse registerToResponse(RegisterAccountResult result) {
        User user = result.user();
        return new RegisterAccountResponse(
                new UserResponse(
                        user.getId().value(),
                        user.getName().firstName(),
                        user.getName().lastName(),
                        result.account().getEmail().value(),
                        user.getPhone().getValue()
                ),
                result.token().value(),
                result.token().expiresAt()
        );
    }

    public static AccountLoginResponse loginToResponse(TokenInfo result) {
        return new AccountLoginResponse(
                result.value(),
                result.expiresAt()
        );
    }

    public static RefreshTokenResponse refreshToResponse(TokenInfo result) {
        return new RefreshTokenResponse(
                result.value(),
                result.expiresAt()
        );
    }
}
