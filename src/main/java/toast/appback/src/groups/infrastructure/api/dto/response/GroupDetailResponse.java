package toast.appback.src.groups.infrastructure.api.dto.response;

import toast.appback.src.debts.infrastructure.api.dto.response.UserSummaryResponse;

import java.util.List;

public record GroupDetailResponse(
        GroupResponse group,
        List<UserSummaryResponse> members
) {
}
