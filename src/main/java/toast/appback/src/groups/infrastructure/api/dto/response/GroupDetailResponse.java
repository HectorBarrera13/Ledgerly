package toast.appback.src.groups.infrastructure.api.dto.response;

import toast.appback.src.users.infrastructure.api.dto.response.UserResponse;

import java.util.List;

public record GroupDetailResponse(
        GroupResponse group,
        List<UserResponse> members
) {
}
