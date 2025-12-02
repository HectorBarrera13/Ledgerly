package toast.appback.src.groups.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import toast.appback.src.groups.application.communication.result.MemberView;
import toast.appback.src.groups.application.port.MemberReadRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.infrastructure.persistence.jparepository.JpaGroupRepository;
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
    private final JpaGroupRepository jpaGroupRepository;
    private final JpaMemberRepository jpaMemberRepository;

    @Override
    public PageResult<MemberView, UUID> findMembersByGroupId(GroupId groupId, PageRequest pageRequest) {

        Page<MemberView> pageable = jpaMemberRepository
                .findMembersByGroupId(groupId.getValue(), PageMapper.toPageable(pageRequest))
                .map(
                        projection -> new MemberView(
                                projection.getUserId(),
                                projection.getFirstName(),
                                projection.getLastName(),
                                projection.getPhone()
                        )
                );
        return PageMapper.toPageResult(pageable);
    }

    @Override
    public PageResult<MemberView, UUID> findMembersByGroupIdAfterCursor(GroupId groupId, CursorRequest<UUID> cursorRequest) {
        Page<MemberView> page = jpaMemberRepository
                .findMembersByGroupId(groupId.getValue(), PageMapper.toPageable(cursorRequest))
                .map(p -> new MemberView(
                        p.getUserId(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getPhone()
                ));

        return PageMapper.toPageResult(page);
    }

    @Override
    public List<MemberEntity> findMembersByGroupIds(List<UUID> groupIds) {
        return jpaMemberRepository.findMembersByGroupIds(groupIds);
    }


}
