package toast.appback.src.groups.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.repository.GroupRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.infrastructure.persistence.jparepository.JpaGroupRepository;
import toast.appback.src.groups.infrastructure.persistence.mapping.GroupMapper;
import toast.model.entities.group.GroupEntity;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryMySQL implements GroupRepository {
    private final JpaGroupRepository jpaGroupRepository;

    @Override
    public void save(Group group) {
        GroupEntity groupEntity = jpaGroupRepository.findByGroupId(group.getId().getValue())
                .orElseGet(GroupEntity::new);
        groupEntity.setGroupId(group.getId().getValue());
        groupEntity.setCreatorId(group.getCreatorId().getValue());
        groupEntity.setName(group.getGroupInformation().getName());
        groupEntity.setDescription(group.getGroupInformation().getDescription());
        groupEntity.setCreatedAt(group.getCreatedAt());
        jpaGroupRepository.save(groupEntity);
    }

    @Override
    public Optional<Group> findById(GroupId id) {
        return jpaGroupRepository.findByGroupId(id.getValue())
                .map(GroupMapper::toDomain);
    }
}
