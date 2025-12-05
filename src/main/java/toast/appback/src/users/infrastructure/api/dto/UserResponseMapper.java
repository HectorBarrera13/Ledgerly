package toast.appback.src.users.infrastructure.api.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import toast.appback.src.auth.application.communication.result.AccountView;
import toast.appback.src.auth.infrastructure.api.dto.AuthMapper;
import toast.appback.src.groups.application.communication.result.MemberView;
import toast.appback.src.users.application.communication.result.FriendView;
import toast.appback.src.users.application.communication.result.UserView;
import toast.appback.src.users.infrastructure.api.dto.response.FriendResponse;
import toast.appback.src.users.infrastructure.api.dto.response.ProfileResponse;
import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;
import toast.appback.src.users.infrastructure.service.UserProfilePictureService;

@Service
@RequiredArgsConstructor
public class UserResponseMapper {
    private final UserProfilePictureService userProfilePictureService;

    public UserResponse toUserResponse(UserView userView) {
        return new UserResponse(
                userView.userId(),
                userView.firstName(),
                userView.lastName(),
                userView.phone()
        );
    }

    public FriendResponse toFriendResponse(FriendView friendView) {
        return new FriendResponse(
                friendView.userId(),
                friendView.firstName(),
                friendView.lastName(),
                friendView.phone(),
                friendView.addedAt(),
                userProfilePictureService.getProfileUri(friendView.userId())
        );
    }

    public ProfileResponse toProfileResponse(AccountView accountView, UserView userView) {
        return new ProfileResponse(
                AuthMapper.toAccountResponse(accountView),
                toUserResponse(userView)
        );
    }

    public UserResponse toUserResponse(MemberView memberView) {
        return new UserResponse(
                memberView.userId(),
                memberView.firstName(),
                memberView.lastName(),
                memberView.phone()
        );
    }
}
