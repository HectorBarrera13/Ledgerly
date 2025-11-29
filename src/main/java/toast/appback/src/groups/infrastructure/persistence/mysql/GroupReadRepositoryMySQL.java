package toast.appback.src.groups.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import toast.appback.src.groups.infrastructure.api.dto.GroupResponseMapper;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupDetailResponse;
import toast.appback.src.groups.infrastructure.api.dto.response.GroupResponse;
import toast.appback.src.shared.application.PageRequest;
import org.springframework.stereotype.Repository;
import toast.appback.src.groups.application.communication.result.GroupView;
import toast.appback.src.groups.application.port.GroupReadRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.infrastructure.persistence.jparepository.JpaGroupRepository;
import toast.appback.src.shared.application.CursorRequest;
import toast.appback.src.shared.application.PageResult;
import toast.appback.src.shared.infrastructure.PageMapper;
import toast.appback.src.users.domain.UserId;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GroupReadRepositoryMySQL implements GroupReadRepository {
    private final JpaGroupRepository jpaGroupRepository;

    @Override
    public Optional<GroupView> findById(GroupId groupId) {
        return jpaGroupRepository.findGroupProjectionByGroupId(groupId.getValue())
                .map(projection -> new GroupView(
                        projection.getGroupId(),
                        projection.getName(),
                        projection.getDescription(),
                        projection.getCreatedAt()
                ));
    }

    @Override
    public PageResult<GroupView, UUID> findGroupsByUserId(UserId userId, PageRequest pageRequest) {

        Page<GroupView> groups = jpaGroupRepository.findAllByUserId(userId.getValue(), PageMapper.toPageable(pageRequest))
                .map(
                        projection -> new GroupView(
                                projection.getGroupId(),
                                projection.getName(),
                                projection.getDescription(),
                                projection.getCreatedAt()
                        )
                );
        return PageMapper.toPageResult(groups);
    }

    @Override
    public PageResult<GroupView, UUID> findGroupsByUserIdAfterCursor(UserId userId, CursorRequest<UUID> cursorRequest) {
        return null;
    }

    @Override
    public Optional<GroupResponse> findGroupByIdAndUser(GroupId groupId, UserId userId) {

        return jpaGroupRepository.findGroupByIdAndUser(groupId.getValue(), userId.getValue())
                .map(projection -> new GroupResponse(
                            projection.getGroupId(),
                            projection.getName(),
                            projection.getDescription(),
                            projection.getCreatedAt()
                ));
    }
}
