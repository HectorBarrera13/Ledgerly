package toast.appback.src.groups.infrastructure.persistence.mapping;

import toast.appback.src.groups.domain.Group;
import toast.appback.src.groups.domain.vo.GroupId;
import toast.appback.src.groups.domain.vo.GroupInformation;
import toast.appback.src.users.domain.UserId;
import toast.model.entities.group.GroupEntity;

import java.time.Instant;

public class GroupMapper {

    public static Group toDomain(GroupEntity groupEntity) {
        if (groupEntity == null) return null;
        return Group.Load(
                GroupId.load(groupEntity.getGroupId()),
                GroupInformation.load(
                        groupEntity.getName(),
                        groupEntity.getDescription()
                ),
                UserId.load(groupEntity.getCreatorId()),
                groupEntity.getCreatedAt()
        );
    }
}
