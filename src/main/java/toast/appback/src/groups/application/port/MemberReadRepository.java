package toast.appback.src.groups.application.port;

import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.shared.application.PageResult;
import toast.model.entities.group.MemberEntity;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de lectura para consultar miembros de un grupo.
 */
public interface MemberReadRepository {
    PageResult<UserSummaryView, UUID> findMembersByGroupId(GroupId groupId, PageRequest pageRequest);

    PageResult<UserSummaryView, UUID> findMembersByGroupIdAfterCursor(GroupId groupId, CursorRequest<UUID> cursorRequest);

    List<MemberEntity> findMembersByGroupIds(List<UUID> groupIds);
}
