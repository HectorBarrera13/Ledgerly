package toast.appback.src.groups.infrastructure.persistence.mysql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import toast.appback.src.groups.application.port.GroupDebtReadRepository;
import toast.appback.src.groups.domain.repository.MemberRepository;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupMember;
import toast.appback.src.groups.infrastructure.persistence.jparepository.JpaGroupRepository;
import toast.appback.src.groups.infrastructure.persistence.jparepository.JpaMemberRepository;
import toast.appback.src.users.infrastructure.persistence.jparepository.JpaUserRepository;
import toast.model.entities.group.GroupEntity;
import toast.model.entities.group.MemberEntity;
import toast.model.entities.users.UserEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryMySQL implements MemberRepository {
    private final JpaMemberRepository jpaMemberRepository;
    private final JpaGroupRepository jpaGroupRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    public void save(GroupMember member) {
        UserEntity userEntity = jpaUserRepository
                .findByUserId(member.getUserId().getValue())
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupEntity groupEntity = jpaGroupRepository
                .findByGroupId(member.getGroupId().getValue())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        MemberEntity entity = new MemberEntity();
        entity.setUser(userEntity);
        entity.setGroup(groupEntity);
        entity.setAddedAt(member.getCreatedAt());

        jpaMemberRepository.save(entity);
    }

    @Override
    public List<GroupMember> findByGroupId(GroupId groupId) {
        return List.of();
    }

    @Override
    public boolean exists(GroupId groupId, GroupMember member) {
        return jpaMemberRepository.existsByGroup_GroupIdAndUser_UserId(
                groupId.getValue(),
                member.getUserId().getValue()
        );
    }


}
