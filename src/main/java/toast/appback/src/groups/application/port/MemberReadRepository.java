package toast.appback.src.groups.application.port;

import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.groups.application.communication.result.MemberView;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageResult;
import toast.model.entities.group.MemberEntity;

import java.util.List;
import java.util.UUID;

public interface MemberReadRepository {
    PageResult<MemberView, UUID> findMembersByGroupId(GroupId groupId, PageRequest pageRequest);
    PageResult<MemberView, UUID> findMembersByGroupIdAfterCursor(GroupId groupId, CursorRequest<UUID> cursorRequest);
    List<MemberEntity> findMembersByGroupIds(List<UUID> groupIds);
}
