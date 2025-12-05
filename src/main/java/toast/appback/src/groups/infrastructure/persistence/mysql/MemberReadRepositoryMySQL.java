package toast.appback.src.groups.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import toast.appback.src.debts.application.communication.result.UserSummaryView;
import toast.appback.src.groups.application.port.MemberReadRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.infrastructure.persistence.jparepository.JpaMemberRepository;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageRequest;
import toast.appback.src.shared.application.PageResult;
import toast.appback.src.shared.infrastructure.PageMapper;
import toast.model.entities.group.MemberEntity;

import java.util.List;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class MemberReadRepositoryMySQL implements MemberReadRepository {
    private final JpaMemberRepository jpaMemberRepository;

    @Override
    public PageResult<UserSummaryView, UUID> findMembersByGroupId(GroupId groupId, PageRequest pageRequest) {

        Page<UserSummaryView> pageable = jpaMemberRepository
                .findMembersByGroupId(groupId.getValue(), PageMapper.toPageable(pageRequest))
                .map(
                        projection -> new UserSummaryView(
                                projection.userId(),
                                projection.userFirstName(),
                                projection.userLastName()
                        )
                );
        return PageMapper.toPageResult(pageable);
    }

    @Override
    public PageResult<UserSummaryView, UUID> findMembersByGroupIdAfterCursor(GroupId groupId, CursorRequest<UUID> cursorRequest) {
        Page<UserSummaryView> page = jpaMemberRepository
                .findMembersByGroupId(groupId.getValue(), PageMapper.toPageable(cursorRequest))
                .map(p -> new UserSummaryView(
                        p.userId(),
                        p.userFirstName(),
                        p.userLastName()
                ));

        return PageMapper.toPageResult(page);
    }

    @Override
    public List<MemberEntity> findMembersByGroupIds(List<UUID> groupIds) {
        return jpaMemberRepository.findMembersByGroupIds(groupIds);
    }


}
