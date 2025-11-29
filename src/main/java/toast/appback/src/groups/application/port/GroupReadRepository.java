package toast.appback.src.groups.application.port;

import toast.appback.src.groups.infrastructure.api.dto.response.GroupDetailResponse;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupResponse;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageResult;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;
import java.util.UUID;

public interface GroupReadRepository {
    Optional<GroupView> findById(GroupId groupId);
    PageResult<GroupView, UUID> findGroupsByUserId(UserId userId, PageRequest pageRequest);
    PageResult<GroupView, UUID> findGroupsByUserIdAfterCursor(UserId userId, CursorRequest<UUID> cursorRequest);
    Optional<GroupResponse> findGroupByIdAndUser(GroupId groupId, UserId userId);
}
