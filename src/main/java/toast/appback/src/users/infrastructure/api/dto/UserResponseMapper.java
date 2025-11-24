package toast.appback.src.users.infrastructure.api.dto;

import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.infrastructure.api.dto.AuthMapper;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.infrastructure.api.dto.response.FriendResponse;
import toast.appback.src.users.infrastructure.api.dto.response.ProfileResponse;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;

public class UserResponseMapper {

    public static UserResponse toUserResponse(UserView userView) {
        return new UserResponse(
                userView.userId(),
                userView.firstName(),
                userView.lastName(),
                userView.phone()
        );
    }

    public static FriendResponse toFriendResponse(FriendView friendView) {
        return new FriendResponse(
                friendView.userId(),
                friendView.firstName(),
                friendView.lastName(),
                friendView.phone(),
                friendView.addedAt()
        );
    }

    public static ProfileResponse toProfileResponse(AccountView accountView, UserView userView) {
        return new ProfileResponse(
                AuthMapper.toAccountResponse(accountView),
                toUserResponse(userView)
        );
    }


}
