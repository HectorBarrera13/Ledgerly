package toast.appback.src.groups.application.port;

import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupDetailResponse;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.users.domain.UserId;

import java.util.List;

public interface GroupReadService {
    List<GroupDetailResponse> getGroupsForUser(UserId userId, PageRequest pageRequest);
    GroupDetailResponse getGroupByIdAndUserId(GroupId groupId, UserId userId, int limit);
}
