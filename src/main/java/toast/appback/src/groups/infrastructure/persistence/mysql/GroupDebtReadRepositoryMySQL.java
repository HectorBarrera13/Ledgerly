package toast.appback.src.groups.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.groups.application.communication.result.GroupDebtView;
import toast.appback.src.groups.application.port.GroupDebtReadRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.shared.application.PageResult;

import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class GroupDebtReadRepositoryMySQL implements GroupDebtReadRepository {
    @Override
    public PageResult<GroupDebtView, UUID> findDebtsDebtByGroupId(GroupId groupId, PageRequest pageRequest) {
        return null;
    }

    @Override
    public PageResult<GroupDebtView, UUID> findDebtsDebtByGroupIdAfterCursor(GroupId groupId, CursorRequest<UUID> cursorRequest) {
        return null;
    }
}
